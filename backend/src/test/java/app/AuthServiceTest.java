package app;

import app.dto.AuthResponse;
import app.dto.LoginRequest;
import app.dto.RegisterRequest;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.exception.BadRequestException;
import app.exception.ConflictException;
import app.exception.UnauthorizedException;
import app.repository.UserRepository;
import com.travelmate.security.JwtUtil;
import app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.travelmate.TravelmateApplication.class)
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final String testEmail = "testuser" + System.currentTimeMillis() + "@example.com";

    @Test
    void testCustomerRegistrationSuccess() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Test Customer")
                .email(testEmail)
                .password("Password@123")
                .role("CUSTOMER")
                .phone("9988776655")
                .build();

        AuthResponse resp = authService.signup(req);

        assertNotNull(resp);
        assertNotNull(resp.getToken());
        assertEquals("Test Customer", resp.getName());
        assertEquals(testEmail, resp.getEmail());
        assertEquals("CUSTOMER", resp.getRole());

        // Verify password is encrypted in database
        User saved = userRepository.findByEmail(testEmail).orElseThrow();
        assertTrue(passwordEncoder.matches("Password@123", saved.getPassword()));
        assertNotEquals("Password@123", saved.getPassword());
    }

    @Test
    void testVendorRegistrationRequiresBusinessName() {
        String vendorEmail = "vendor" + System.currentTimeMillis() + "@example.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Test Vendor")
                .email(vendorEmail)
                .password("Password@123")
                .role("VENDOR")
                .businessName("") // empty business name
                .build();

        assertThrows(BadRequestException.class, () -> authService.signup(req));
    }

    @Test
    void testAdminRegistrationRejected() {
        String adminEmail = "fakeadmin" + System.currentTimeMillis() + "@example.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Fake Admin")
                .email(adminEmail)
                .password("Password@123")
                .role("ADMIN")
                .build();

        assertThrows(BadRequestException.class, () -> authService.signup(req));
    }

    @Test
    void testDuplicateEmailRegistrationThrowsConflict() {
        RegisterRequest req1 = RegisterRequest.builder()
                .name("First User")
                .email(testEmail)
                .password("Password@123")
                .role("CUSTOMER")
                .build();
        authService.signup(req1);

        RegisterRequest req2 = RegisterRequest.builder()
                .name("Second User")
                .email(testEmail)
                .password("Different@123")
                .role("CUSTOMER")
                .build();

        assertThrows(ConflictException.class, () -> authService.signup(req2));
    }

    @Test
    void testLoginSuccessAndJwtValidation() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Login User")
                .email(testEmail)
                .password("Secret@123")
                .role("CUSTOMER")
                .build();
        authService.signup(req);

        LoginRequest loginReq = LoginRequest.builder()
                .email(testEmail)
                .password("Secret@123")
                .build();

        AuthResponse loginResp = authService.login(loginReq);
        assertNotNull(loginResp.getToken());
        assertTrue(jwtUtil.isTokenValid(loginResp.getToken()));
        assertEquals(testEmail, jwtUtil.extractEmail(loginResp.getToken()));
        assertEquals("CUSTOMER", jwtUtil.extractRole(loginResp.getToken()));
    }

    @Test
    void testLoginFailureInvalidPassword() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Login User")
                .email(testEmail)
                .password("Secret@123")
                .role("CUSTOMER")
                .build();
        authService.signup(req);

        LoginRequest loginReq = LoginRequest.builder()
                .email(testEmail)
                .password("WrongPassword")
                .build();

        assertThrows(UnauthorizedException.class, () -> authService.login(loginReq));
    }
}
