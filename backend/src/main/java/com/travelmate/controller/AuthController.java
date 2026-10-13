package com.travelmate.controller;

import com.travelmate.dto.AuthResponse;
import com.travelmate.dto.LoginRequest;
import com.travelmate.dto.RegisterRequest;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import com.travelmate.repository.UserRepository;
import com.travelmate.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping({"/register", "/signup"})
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        // 1. Reject duplicate email
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "An account with this email already exists."));
        }

        // 2. Reject public ADMIN registration
        String roleStr = req.getRole() != null ? req.getRole().trim().toUpperCase() : "CUSTOMER";
        if ("ADMIN".equals(roleStr)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Administrator accounts cannot be created through public registration."));
        }

        Role role = "VENDOR".equals(roleStr) ? Role.VENDOR : Role.CUSTOMER;

        // 3. Handle optional business name for vendors
        String businessName = null;
        if (role == Role.VENDOR && req.getBusinessName() != null && !req.getBusinessName().isBlank()) {
            businessName = req.getBusinessName().trim();
        }

        // 4. Save user with BCrypt password hash
        User user = User.builder()
                .name(req.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .businessName(businessName)
                .phone(req.getPhone() != null ? req.getPhone().trim() : null)
                .address(req.getAddress() != null ? req.getAddress().trim() : null)
                .build();

        User savedUser = userRepository.save(user);

        // 5. Generate signed JWT token
        String token = jwtUtil.generateToken(savedUser);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .businessName(savedUser.getBusinessName())
                .phone(savedUser.getPhone())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }

        String token = jwtUtil.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .businessName(user.getBusinessName())
                .phone(user.getPhone())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated."));
        }

        if (auth.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "businessName", user.getBusinessName() != null ? user.getBusinessName() : "",
                    "phone", user.getPhone() != null ? user.getPhone() : ""
            ));
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "businessName", user.getBusinessName() != null ? user.getBusinessName() : "",
                "phone", user.getPhone() != null ? user.getPhone() : ""
        ));
    }
}
