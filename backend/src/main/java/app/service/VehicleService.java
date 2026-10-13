package app.service;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.VehicleRequest;
import app.dto.VehicleResponse;
import app.entity.*;
import app.exception.BadRequestException;
import app.exception.ConflictException;
import app.exception.ResourceNotFoundException;
import app.repository.BookingRepository;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public VehicleResponse createVehicle(VehicleRequest req, Long vendorId) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor account not found."));

        boolean autoGen = (req.getRegistrationNumber() == null || req.getRegistrationNumber().isBlank());
        String regNo = !autoGen
                ? req.getRegistrationNumber().trim().toUpperCase()
                : "TS09-" + (char)('A' + (int)(Math.random() * 26)) + (char)('A' + (int)(Math.random() * 26)) + "-" + (1000 + (int)(Math.random() * 9000));

        if (!autoGen && vehicleRepository.existsByRegistrationNumber(regNo)) {
            throw new ConflictException("Vehicle with registration number " + regNo + " already exists.");
        }
        if (autoGen && vehicleRepository.existsByRegistrationNumber(regNo)) {
            regNo = regNo + "-" + (System.currentTimeMillis() % 1000);
        }

        VehicleType type = req.getVehicleType();
        if (type == null && req.getType() != null) {
            String t = req.getType().trim().toUpperCase();
            if ("HATCHBACK".equals(t)) type = VehicleType.HATCHBACK;
            else if ("SEDAN".equals(t)) type = VehicleType.SEDAN;
            else if ("SUV".equals(t)) type = VehicleType.SUV;
            else if ("BUS".equals(t)) type = VehicleType.BUS;
            else if ("VAN".equals(t)) type = VehicleType.VAN;
            else type = VehicleType.CAR;
        }
        if (type == null) type = VehicleType.CAR;

        java.math.BigDecimal price = req.getRentalPricePerDay() != null
                ? req.getRentalPricePerDay()
                : (req.getPricePerDay() != null ? req.getPricePerDay() : new java.math.BigDecimal("2000.00"));

        String brand = (req.getBrand() != null && !req.getBrand().isBlank())
                ? req.getBrand().trim()
                : (req.getName().contains(" ") ? req.getName().split(" ")[0] : "TravelMate Fleet");

        String model = (req.getModel() != null && !req.getModel().isBlank())
                ? req.getModel().trim()
                : req.getName().trim();

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(regNo)
                .vehicleType(type)
                .brand(brand)
                .model(model)
                .name(req.getName().trim())
                .seatingCapacity(req.getSeatingCapacity() != null ? req.getSeatingCapacity() : 5)
                .fuelType(req.getFuelType() != null && !req.getFuelType().isBlank() ? req.getFuelType().trim() : "Petrol")
                .currentKm(req.getCurrentKm() != null ? req.getCurrentKm() : 0)
                .insuranceExpiryDate(req.getInsuranceExpiryDate())
                .rcExpiryDate(req.getRcExpiryDate())
                .permitExpiryDate(req.getPermitExpiryDate())
                .rentalPricePerDay(price)
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .status(req.getStatus() != null ? req.getStatus() : VehicleStatus.PENDING_APPROVAL)
                .vendor(vendor)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    @Transactional
    public VehicleResponse approveVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        vehicle.setStatus(VehicleStatus.APPROVED);
        return mapToResponse(vehicleRepository.save(vehicle));
    }

    public List<VehicleResponse> getPendingVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.PENDING_APPROVAL).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest req, Long vendorId, boolean isAdmin) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!isAdmin && !vehicle.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only modify vehicles belonging to your business.");
        }

        String regNo = req.getRegistrationNumber().trim().toUpperCase();
        if (vehicleRepository.existsByRegistrationNumberAndIdNot(regNo, id)) {
            throw new ConflictException("Registration number " + regNo + " is already used by another vehicle.");
        }

        vehicle.setRegistrationNumber(regNo);
        vehicle.setVehicleType(req.getVehicleType());
        vehicle.setBrand(req.getBrand().trim());
        vehicle.setModel(req.getModel().trim());
        vehicle.setName(req.getName().trim());
        vehicle.setSeatingCapacity(req.getSeatingCapacity());
        vehicle.setFuelType(req.getFuelType().trim());
        vehicle.setCurrentKm(req.getCurrentKm());
        vehicle.setInsuranceExpiryDate(req.getInsuranceExpiryDate());
        vehicle.setRcExpiryDate(req.getRcExpiryDate());
        vehicle.setPermitExpiryDate(req.getPermitExpiryDate());
        vehicle.setRentalPricePerDay(req.getRentalPricePerDay());
        vehicle.setDescription(req.getDescription());
        vehicle.setImageUrl(req.getImageUrl());
        if (req.getStatus() != null) {
            vehicle.setStatus(req.getStatus());
        }

        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return mapToResponse(vehicle);
    }

    public Page<VehicleResponse> getVehiclesByVendor(Long vendorId, Pageable pageable) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));
        return vehicleRepository.findByVendor(vendor, pageable).map(this::mapToResponse);
    }

    public Page<VehicleResponse> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<VehicleResponse> searchPublicVehicles(
            VehicleType type, Integer minCapacity, String search, Pageable pageable) {
        return vehicleRepository.searchVehicles(VehicleStatus.AVAILABLE, type, minCapacity, search, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public VehicleResponse updateVehicleStatus(Long id, VehicleStatus status, Long vendorId, boolean isAdmin) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!isAdmin && !vehicle.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only modify your own vehicles.");
        }

        vehicle.setStatus(status);
        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long id, Long vendorId, boolean isAdmin) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!isAdmin && !vehicle.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only delete your own vehicles.");
        }

        // Check if there are active bookings
        List<Booking> activeBookings = bookingRepository.findByVehicle(vehicle).stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED ||
                             b.getBookingStatus() == BookingStatus.IN_PROGRESS ||
                             b.getBookingStatus() == BookingStatus.PENDING)
                .toList();

        if (!activeBookings.isEmpty()) {
            throw new BadRequestException("Cannot delete vehicle because it has active or scheduled bookings. Mark it INACTIVE instead.");
        }

        // Soft-delete to preserve booking history and financial reports
        vehicle.setStatus(VehicleStatus.INACTIVE);
        vehicleRepository.save(vehicle);
    }

    public List<VehicleResponse> getExpiringVehicles(Long vendorId, boolean isAdmin) {
        LocalDate threshold = LocalDate.now().plusDays(30);
        List<Vehicle> list;
        if (isAdmin) {
            list = vehicleRepository.findAllExpiringVehicles(threshold);
        } else {
            User vendor = userRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));
            list = vehicleRepository.findExpiringVehiclesForVendor(vendor, threshold);
        }
        return list.stream().map(this::mapToResponse).toList();
    }

    public VehicleResponse mapToResponse(Vehicle v) {
        LocalDate now = LocalDate.now();
        LocalDate warningThreshold = now.plusDays(30);

        List<String> warnings = new ArrayList<>();
        boolean expired = false;
        boolean expiringSoon = false;

        if (v.getInsuranceExpiryDate() != null) {
            if (v.getInsuranceExpiryDate().isBefore(now)) {
                warnings.add("Insurance expired on " + v.getInsuranceExpiryDate());
                expired = true;
            } else if (v.getInsuranceExpiryDate().isBefore(warningThreshold)) {
                warnings.add("Insurance expiring soon on " + v.getInsuranceExpiryDate());
                expiringSoon = true;
            }
        }

        if (v.getRcExpiryDate() != null) {
            if (v.getRcExpiryDate().isBefore(now)) {
                warnings.add("RC registration expired on " + v.getRcExpiryDate());
                expired = true;
            } else if (v.getRcExpiryDate().isBefore(warningThreshold)) {
                warnings.add("RC registration expiring soon on " + v.getRcExpiryDate());
                expiringSoon = true;
            }
        }

        if (v.getPermitExpiryDate() != null) {
            if (v.getPermitExpiryDate().isBefore(now)) {
                warnings.add("Commercial permit expired on " + v.getPermitExpiryDate());
                expired = true;
            } else if (v.getPermitExpiryDate().isBefore(warningThreshold)) {
                warnings.add("Commercial permit expiring soon on " + v.getPermitExpiryDate());
                expiringSoon = true;
            }
        }

        return VehicleResponse.builder()
                .id(v.getId())
                .registrationNumber(v.getRegistrationNumber())
                .vehicleType(v.getVehicleType())
                .brand(v.getBrand())
                .model(v.getModel())
                .name(v.getName())
                .seatingCapacity(v.getSeatingCapacity())
                .fuelType(v.getFuelType())
                .currentKm(v.getCurrentKm())
                .insuranceExpiryDate(v.getInsuranceExpiryDate())
                .rcExpiryDate(v.getRcExpiryDate())
                .permitExpiryDate(v.getPermitExpiryDate())
                .rentalPricePerDay(v.getRentalPricePerDay())
                .description(v.getDescription())
                .imageUrl(v.getImageUrl())
                .status(v.getStatus())
                .vendorId(v.getVendor().getId())
                .vendorName(v.getVendor().getName())
                .vendorBusinessName(v.getVendor().getBusinessName())
                .hasExpiredDocuments(expired)
                .hasExpiringSoonDocuments(expiringSoon)
                .warnings(warnings)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
