package app.controller;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.MaintenanceRequest;
import app.dto.MaintenanceResponse;
import com.travelmate.model.Role;
import app.security.UserPrincipal;
import app.service.MaintenanceService;
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
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceResponse> scheduleMaintenance(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MaintenanceRequest request) {
        MaintenanceResponse created = maintenanceService.scheduleMaintenance(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceResponse>> getMaintenance(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "serviceDate"));
        return ResponseEntity.ok(maintenanceService.getMaintenanceByVendor(principal.getId(), pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> updateMaintenance(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MaintenanceRequest request) {
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        return ResponseEntity.ok(maintenanceService.updateMaintenance(id, request, principal.getId(), isAdmin));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MaintenanceResponse>> getUpcomingMaintenance(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(maintenanceService.getUpcomingMaintenance(principal.getId()));
    }
}
