package app.controller;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.ApiResponse;
import app.dto.DriverRequest;
import app.dto.DriverResponse;
import app.entity.DriverStatus;
import com.travelmate.model.Role;
import app.security.UserPrincipal;
import app.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getDrivers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(driverService.getDriversByVendor(principal.getId(), status, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(driverService.getAvailableDrivers(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(driverService.getDriverById(id, principal.getId(), isAdmin));
    }

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<DriverResponse> createDriver(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DriverRequest request) {
        DriverResponse created = driverService.createDriver(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DriverRequest request) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(driverService.updateDriver(id, request, principal.getId(), isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDriver(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        driverService.deleteDriver(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Driver status set to inactive."));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<DriverResponse>> getExpiringDrivers(
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(driverService.getExpiringDrivers(principal.getId(), isAdmin));
    }
}
