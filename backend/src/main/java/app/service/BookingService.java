package app.service;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.BookingRequest;
import app.dto.BookingResponse;
import app.entity.*;
import app.exception.BadRequestException;
import app.exception.ConflictException;
import app.exception.ResourceNotFoundException;
import app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest req, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found."));

        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE && vehicle.getStatus() != VehicleStatus.APPROVED) {
            throw new BadRequestException("Vehicle is currently " + vehicle.getStatus() + " and cannot be booked.");
        }

        if (req.getPickupDateTime() == null && req.getFromDate() != null) {
            try {
                LocalDate d = LocalDate.parse(req.getFromDate().trim());
                req.setPickupDateTime(d.isEqual(LocalDate.now()) ? LocalDateTime.now().plusMinutes(10) : d.atTime(9, 0));
            } catch (Exception e) {
                req.setPickupDateTime(LocalDateTime.now().plusMinutes(10));
            }
        }
        if (req.getPickupDateTime() == null) {
            req.setPickupDateTime(LocalDateTime.now().plusMinutes(10));
        }

        if (req.getReturnDateTime() == null && req.getToDate() != null) {
            try {
                req.setReturnDateTime(LocalDate.parse(req.getToDate().trim()).atTime(18, 0));
            } catch (Exception e) {
                req.setReturnDateTime(req.getPickupDateTime().plusDays(1));
            }
        }
        if (req.getReturnDateTime() == null) {
            req.setReturnDateTime(req.getPickupDateTime().plusDays(1));
        }

        if (req.getPickupLocation() == null || req.getPickupLocation().isBlank()) {
            req.setPickupLocation("Rental Hub");
        }
        if (req.getDestination() == null || req.getDestination().isBlank()) {
            req.setDestination("Round Trip");
        }
        if (req.getNumberOfPassengers() == null) {
            req.setNumberOfPassengers(1);
        }

        if (req.getPickupDateTime().isAfter(req.getReturnDateTime())) {
            throw new BadRequestException("Return date and time must be after pickup date and time.");
        }

        if (req.getPickupDateTime().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new BadRequestException("Pickup date cannot be in the past.");
        }

        if (req.getNumberOfPassengers() > vehicle.getSeatingCapacity()) {
            throw new BadRequestException("Vehicle seating capacity is " + vehicle.getSeatingCapacity() +
                    ", but " + req.getNumberOfPassengers() + " passengers were requested.");
        }

        // 1. Check vehicle overlap conflicts
        List<Booking> vehicleConflicts = bookingRepository.findConflictingVehicleBookings(
                vehicle.getId(), req.getPickupDateTime(), req.getReturnDateTime(), null);

        if (!vehicleConflicts.isEmpty()) {
            throw new ConflictException("Vehicle is already booked for overlapping dates. Please select different dates.");
        }

        // 2. Validate optional driver
        Driver driver = null;
        if (req.getDriverId() != null) {
            driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found."));

            if (!driver.getVendor().getId().equals(vehicle.getVendor().getId())) {
                throw new BadRequestException("The selected driver does not belong to this vehicle's rental company.");
            }

            if (driver.getLicenceExpiryDate() != null && driver.getLicenceExpiryDate().isBefore(LocalDate.now())) {
                throw new BadRequestException("Driver licence has expired and cannot be assigned to trips.");
            }

            List<Booking> driverConflicts = bookingRepository.findConflictingDriverBookings(
                    driver.getId(), req.getPickupDateTime(), req.getReturnDateTime(), null);

            if (!driverConflicts.isEmpty()) {
                throw new ConflictException("The selected driver is already assigned to another trip during this timeframe.");
            }
        }

        // 3. Calculate server-side rental cost
        long days = Math.max(1, ChronoUnit.DAYS.between(
                req.getPickupDateTime().toLocalDate(), req.getReturnDateTime().toLocalDate()) + 1);

        BigDecimal rentalCost = vehicle.getRentalPricePerDay().multiply(BigDecimal.valueOf(days));
        BigDecimal addCharges = req.getAdditionalCharges() != null && req.getAdditionalCharges().compareTo(BigDecimal.ZERO) > 0
                ? req.getAdditionalCharges()
                : BigDecimal.ZERO;
        BigDecimal totalAmount = rentalCost.add(addCharges);

        // 4. Generate unique booking reference
        String reference = "TM-" + System.currentTimeMillis() % 100000000 + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Booking booking = Booking.builder()
                .bookingReference(reference)
                .customer(customer)
                .vendor(vehicle.getVendor())
                .vehicle(vehicle)
                .driver(driver)
                .pickupLocation(req.getPickupLocation().trim())
                .destination(req.getDestination().trim())
                .pickupDateTime(req.getPickupDateTime())
                .returnDateTime(req.getReturnDateTime())
                .numberOfPassengers(req.getNumberOfPassengers())
                .rentalPrice(rentalCost)
                .additionalCharges(addCharges)
                .totalAmount(totalAmount)
                .bookingStatus(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);

        // 5. Send in-app notification to Customer and Vendor
        notificationRepository.save(Notification.builder()
                .recipient(customer)
                .title("Booking Confirmed: " + reference)
                .message("Your booking for " + vehicle.getName() + " from " +
                        req.getPickupLocation() + " to " + req.getDestination() + " is confirmed.")
                .type(NotificationType.BOOKING_CONFIRMED)
                .referenceId(saved.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(vehicle.getVendor())
                .title("New Booking Received: " + reference)
                .message("New reservation for " + vehicle.getName() + " by " + customer.getName() + ".")
                .type(NotificationType.BOOKING_REQUEST)
                .referenceId(saved.getId())
                .build());

        return mapToResponse(saved);
    }

    public Page<BookingResponse> getBookings(Long userId, Role role, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (role == Role.ADMIN) {
            return bookingRepository.findAll(pageable).map(this::mapToResponse);
        } else if (role == Role.VENDOR) {
            return bookingRepository.findByVendor(user, pageable).map(this::mapToResponse);
        } else {
            return bookingRepository.findByCustomer(user, pageable).map(this::mapToResponse);
        }
    }

    public BookingResponse getBookingById(Long id, Long userId, Role role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (role == Role.CUSTOMER && !booking.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have access to this booking.");
        }
        if (role == Role.VENDOR && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("You can only access bookings for your business.");
        }

        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse updateTripStatus(Long id, BookingStatus newStatus, Long userId, Role role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (role != Role.ADMIN && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("Only the assigned vendor or an administrator can update trip status.");
        }

        booking.setBookingStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        // Notify customer about status change
        notificationRepository.save(Notification.builder()
                .recipient(booking.getCustomer())
                .title("Trip Status Updated: " + booking.getBookingReference())
                .message("Your trip status is now " + newStatus.name() + ".")
                .type(NotificationType.UPCOMING_TRIP)
                .referenceId(updated.getId())
                .build());

        return mapToResponse(updated);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, Long userId, Role role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (role == Role.CUSTOMER && !booking.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("You can only cancel your own bookings.");
        }
        if (role == Role.VENDOR && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("You can only cancel bookings for your business.");
        }

        if (booking.getBookingStatus() == BookingStatus.IN_PROGRESS ||
            booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a trip that is already in progress or completed.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        // Notify customer and vendor
        notificationRepository.save(Notification.builder()
                .recipient(booking.getCustomer())
                .title("Booking Cancelled: " + booking.getBookingReference())
                .message("Booking " + booking.getBookingReference() + " has been cancelled.")
                .type(NotificationType.BOOKING_CANCELLED)
                .referenceId(updated.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(booking.getVendor())
                .title("Booking Cancelled: " + booking.getBookingReference())
                .message("Booking " + booking.getBookingReference() + " has been cancelled.")
                .type(NotificationType.BOOKING_CANCELLED)
                .referenceId(updated.getId())
                .build());

        return mapToResponse(updated);
    }

    @Transactional
    public BookingResponse assignDriver(Long bookingId, Long driverId, Long userId, Role role) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (role != Role.ADMIN && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("You can only assign drivers to your own bookings.");
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found."));

        if (!driver.getVendor().getId().equals(booking.getVendor().getId())) {
            throw new BadRequestException("Driver does not belong to this vendor.");
        }

        List<Booking> driverConflicts = bookingRepository.findConflictingDriverBookings(
                driverId, booking.getPickupDateTime(), booking.getReturnDateTime(), booking.getId());

        if (!driverConflicts.isEmpty()) {
            throw new ConflictException("Driver is already booked for another trip during this interval.");
        }

        booking.setDriver(driver);
        return mapToResponse(bookingRepository.save(booking));
    }

    public BookingResponse mapToResponse(Booking b) {
        long days = Math.max(1, ChronoUnit.DAYS.between(
                b.getPickupDateTime().toLocalDate(), b.getReturnDateTime().toLocalDate()) + 1);

        BigDecimal paid = paymentRepository.sumPaidAmountForBooking(b.getId());
        BigDecimal balance = b.getTotalAmount().subtract(paid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }

        return BookingResponse.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .customerId(b.getCustomer().getId())
                .customerName(b.getCustomer().getName())
                .customerEmail(b.getCustomer().getEmail())
                .customerPhone(b.getCustomer().getPhone())
                .vendorId(b.getVendor().getId())
                .vendorName(b.getVendor().getName())
                .vendorBusinessName(b.getVendor().getBusinessName())
                .vehicleId(b.getVehicle().getId())
                .vehicleName(b.getVehicle().getName())
                .vehicleRegistrationNumber(b.getVehicle().getRegistrationNumber())
                .vehicleType(b.getVehicle().getVehicleType().name())
                .rentalPricePerDay(b.getVehicle().getRentalPricePerDay())
                .vehicleImageUrl(b.getVehicle().getImageUrl())
                .driverId(b.getDriver() != null ? b.getDriver().getId() : null)
                .driverName(b.getDriver() != null ? b.getDriver().getName() : null)
                .driverPhone(b.getDriver() != null ? b.getDriver().getPhone() : null)
                .pickupLocation(b.getPickupLocation())
                .destination(b.getDestination())
                .pickupDateTime(b.getPickupDateTime())
                .returnDateTime(b.getReturnDateTime())
                .numberOfPassengers(b.getNumberOfPassengers())
                .totalDays(days)
                .rentalPrice(b.getRentalPrice())
                .additionalCharges(b.getAdditionalCharges())
                .totalAmount(b.getTotalAmount())
                .totalPaid(paid)
                .balanceDue(balance)
                .bookingStatus(b.getBookingStatus())
                .paymentStatus(b.getPaymentStatus())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
