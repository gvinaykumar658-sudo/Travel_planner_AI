package app.controller;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.CustomerSummaryResponse;
import app.security.UserPrincipal;
import app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerSummaryResponse>> getCustomers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(customerService.getCustomers(principal.getId(), principal.getUser().getRole(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSummaryResponse> getCustomerById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(customerService.getCustomerDetails(id, principal.getId(), principal.getUser().getRole()));
    }
}
