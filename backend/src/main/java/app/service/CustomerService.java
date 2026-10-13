package app.service;

import app.dto.CustomerSummaryResponse;
import app.entity.Booking;
import app.entity.BookingStatus;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import org.springframework.security.access.AccessDeniedException;
import app.exception.ResourceNotFoundException;
import app.repository.BookingRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public Page<CustomerSummaryResponse> getCustomers(Long vendorId, Role role, Pageable pageable) {
        if (role == Role.ADMIN) {
            Page<User> customers = userRepository.findByRole(Role.CUSTOMER, pageable);
            return customers.map(c -> buildSummary(c, null));
        }

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));

        List<User> distinctCustomers = bookingRepository.findDistinctCustomersByVendor(vendor);

        // Paginate in memory or slice
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), distinctCustomers.size());

        List<CustomerSummaryResponse> content = (start <= end && start < distinctCustomers.size())
                ? distinctCustomers.subList(start, end).stream().map(c -> buildSummary(c, vendor)).toList()
                : List.of();

        return new PageImpl<>(content, pageable, distinctCustomers.size());
    }

    public CustomerSummaryResponse getCustomerDetails(Long customerId, Long vendorId, Role role) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (role == Role.ADMIN) {
            return buildSummary(customer, null);
        }

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));

        List<Booking> bookingsWithVendor = bookingRepository.findByCustomer(customer).stream()
                .filter(b -> b.getVendor().getId().equals(vendorId))
                .toList();

        if (bookingsWithVendor.isEmpty()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot access customer information for travelers who have not booked with your business.");
        }

        return buildSummary(customer, vendor);
    }

    private CustomerSummaryResponse buildSummary(User customer, User filterVendor) {
        List<Booking> bookings = bookingRepository.findByCustomer(customer);
        if (filterVendor != null) {
            bookings = bookings.stream().filter(b -> b.getVendor().getId().equals(filterVendor.getId())).toList();
        }

        long count = bookings.size();
        BigDecimal totalSpent = bookings.stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime lastBooking = bookings.stream()
                .map(Booking::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return CustomerSummaryResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .totalBookings(count)
                .totalSpent(totalSpent)
                .lastBookingDate(lastBooking)
                .memberSince(customer.getCreatedAt())
                .build();
    }
}
