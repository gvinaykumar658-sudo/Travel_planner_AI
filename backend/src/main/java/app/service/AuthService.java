package app.service;

import app.dto.AuthResponse;
import app.dto.LoginRequest;
import app.dto.RegisterRequest;
import app.dto.UserResponse;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.exception.BadRequestException;
import app.exception.ConflictException;
import app.exception.UnauthorizedException;
import app.repository.UserRepository;
import com.travelmate.security.JwtUtil;
import app.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse signup(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // 1. Check duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists.");
        }

        // 2. Validate role: prevent public registration of ADMIN
        String roleStr = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole().trim().toUpperCase()
                : "CUSTOMER";

        if ("ADMIN".equals(roleStr)) {
            throw new BadRequestException("Administrator accounts cannot be created through public registration.");
        }

        Role assignedRole;
        try {
            assignedRole = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account type. Allowed types are CUSTOMER or VENDOR.");
        }

        // 3. For VENDOR, validate business name
        String businessName = request.getBusinessName();
        if (assignedRole == Role.VENDOR) {
            if (businessName == null || businessName.trim().isBlank()) {
                throw new BadRequestException("Business name is required for vendor accounts.");
            }
            businessName = businessName.trim();
        }

        // 4. Create and persist user with BCrypt hashed password
        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .businessName(businessName)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .address(request.getAddress() != null ? request.getAddress().trim() : null)
                .build();

        User savedUser = userRepository.save(user);

        // 5. Generate signed JWT token
        String token = jwtUtil.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .businessName(savedUser.getBusinessName())
                .phone(savedUser.getPhone())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .businessName(user.getBusinessName())
                .phone(user.getPhone())
                .build();
    }

    public UserResponse getCurrentUser(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new UnauthorizedException("Authentication token is missing or invalid.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UnauthorizedException("User profile not found."));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .businessName(user.getBusinessName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
