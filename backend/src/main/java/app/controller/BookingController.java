package app.controller;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.BookingRequest;
import app.dto.BookingResponse;
import app.entity.BookingStatus;
import app.security.UserPrincipal;
import app.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.createBooking(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<?> getBookings(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BookingResponse> p = bookingService.getBookings(principal.getId(), principal.getUser().getRole(), pageable);
        if (principal.getUser().getRole() == Role.ADMIN) {
            return ResponseEntity.ok(p.getContent());
        }
        return ResponseEntity.ok(p.getContent());
    }

    @GetMapping("/mine")
    public ResponseEntity<java.util.List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserPrincipal principal) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(bookingService.getBookings(principal.getId(), Role.CUSTOMER, pageable).getContent());
    }

    @GetMapping("/vendor")
    public ResponseEntity<java.util.List<BookingResponse>> getVendorBookings(
            @AuthenticationPrincipal UserPrincipal principal) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(bookingService.getBookings(principal.getId(), Role.VENDOR, pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(bookingService.getBookingById(id, principal.getId(), principal.getUser().getRole()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(bookingService.updateTripStatus(id, status, principal.getId(), principal.getUser().getRole()));
    }

    @RequestMapping(value = "/{id}/cancel", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, principal.getId(), principal.getUser().getRole()));
    }

    @PatchMapping("/{id}/driver")
    public ResponseEntity<BookingResponse> assignDriver(
            @PathVariable Long id,
            @RequestParam Long driverId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(bookingService.assignDriver(id, driverId, principal.getId(), principal.getUser().getRole()));
    }
}
