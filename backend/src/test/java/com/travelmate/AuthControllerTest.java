package com.travelmate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.dto.LoginRequest;
import com.travelmate.dto.RegisterRequest;
import com.travelmate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterCustomerSuccess() throws Exception {
        String email = "customer" + System.currentTimeMillis() + "@test.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Ramesh Varma")
                .email(email)
                .password("Password@123")
                .role("CUSTOMER")
                .phone("9876543210")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.name").value("Ramesh Varma"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void testRegisterDuplicateEmailReturnsConflict() throws Exception {
        String email = "dup" + System.currentTimeMillis() + "@test.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("First User")
                .email(email)
                .password("Password@123")
                .role("CUSTOMER")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // Attempt second registration with same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("An account with this email already exists."));
    }

    @Test
    void testRegisterAdminRejected() throws Exception {
        String email = "admin" + System.currentTimeMillis() + "@test.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Fake Admin")
                .email(email)
                .password("Password@123")
                .role("ADMIN")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Administrator accounts cannot be created through public registration."));
    }

    @Test
    void testLoginSuccess() throws Exception {
        String email = "login" + System.currentTimeMillis() + "@test.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Suresh Raina")
                .email(email)
                .password("Password@123")
                .role("CUSTOMER")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = LoginRequest.builder()
                .email(email)
                .password("Password@123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testLoginInvalidPasswordReturnsUnauthorized() throws Exception {
        String email = "loginfail" + System.currentTimeMillis() + "@test.com";
        RegisterRequest req = RegisterRequest.builder()
                .name("Failed Login")
                .email(email)
                .password("Password@123")
                .role("CUSTOMER")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = LoginRequest.builder()
                .email(email)
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }
}
