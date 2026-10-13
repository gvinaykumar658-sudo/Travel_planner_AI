package app.controller;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.ApiResponse;
import app.dto.VehicleRequest;
import app.dto.VehicleResponse;
import com.travelmate.model.Role;
import app.entity.VehicleStatus;
import app.entity.VehicleType;
import app.security.UserPrincipal;
import app.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // Public vehicle search & catalog
    @GetMapping("/public")
    public ResponseEntity<List<VehicleResponse>> searchPublicVehicles(
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Sort sortObj = Sort.by(Sort.Direction.DESC, "id");
        if (sort.length >= 2) {
            sortObj = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        String effectiveSearch = search;
        if ((effectiveSearch == null || effectiveSearch.isBlank()) && location != null && !location.isBlank()) {
            effectiveSearch = location.trim();
        }

        return ResponseEntity.ok(vehicleService.searchPublicVehicles(type, minCapacity, effectiveSearch, pageable).getContent());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<VehicleResponse> getPublicVehicleDetails(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    // Authenticated management endpoints
    @GetMapping
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getVehicles(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        if (principal.getUser().getRole() == Role.ADMIN) {
            return ResponseEntity.ok(vehicleService.getAllVehicles(pageable).getContent());
        }

        return ResponseEntity.ok(vehicleService.getVehiclesByVendor(principal.getId(), pageable).getContent());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(vehicleService.getVehiclesByVendor(principal.getId(), Pageable.unpaged()).getContent());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getPendingVehicles() {
        return ResponseEntity.ok(vehicleService.getPendingVehicles());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> approveVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.approveVehicle(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VehicleResponse> createVehicle(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse created = vehicleService.createVehicle(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VehicleRequest request) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request, principal.getId(), isAdmin));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<VehicleResponse> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam VehicleStatus status) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status, principal.getId(), isAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        vehicleService.deleteVehicle(id, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deactivated successfully."));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getExpiringVehicles(
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(vehicleService.getExpiringVehicles(principal.getId(), isAdmin));
    }
}
