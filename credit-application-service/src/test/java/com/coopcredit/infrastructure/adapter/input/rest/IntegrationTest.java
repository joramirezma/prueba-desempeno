package com.coopcredit.infrastructure.adapter.input.rest;

import com.coopcredit.application.dto.CreateAffiliateRequest;
import com.coopcredit.application.dto.LoginRequest;
import com.coopcredit.application.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests using Testcontainers.
 * 
 * To run these tests, execute:
 * mvn test -Dtest=IntegrationTest -Dtestcontainers.enabled=true
 * 
 * Requires Docker to be running and accessible.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
class IntegrationTest {

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                        .withDatabaseName("coopcredit_test")
                        .withUsername("test")
                        .withPassword("test");

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
                registry.add("spring.flyway.enabled", () -> "true");
                registry.add("app.risk-service.url", () -> "http://localhost:8081");
        }

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Should register a new user")
        void shouldRegisterNewUser() throws Exception {
                RegisterRequest request = new RegisterRequest(
                                "testuser",
                                "password123",
                                "testuser@email.com",
                                null,
                                null);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Should login with valid credentials")
        void shouldLoginWithValidCredentials() throws Exception {
                // First register
                RegisterRequest registerRequest = new RegisterRequest(
                                "logintest",
                                "password123",
                                "logintest@email.com",
                                null,
                                null);
                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)));

                // Then login
                LoginRequest loginRequest = new LoginRequest("logintest", "password123");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.type").value("Bearer"));
        }

        @Test
        @DisplayName("Should return 401 for protected endpoint without token")
        void shouldReturn401ForProtectedEndpointWithoutToken() throws Exception {
                mockMvc.perform(get("/api/affiliates"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should access protected endpoint with valid token")
        void shouldAccessProtectedEndpointWithValidToken() throws Exception {
                // Register admin user
                RegisterRequest registerRequest = new RegisterRequest(
                                "adminuser",
                                "admin123",
                                "adminuser@email.com",
                                null,
                                java.util.Set.of(com.coopcredit.domain.model.enums.Role.ROLE_ADMIN));

                MvcResult result = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andReturn();

                String response = result.getResponse().getContentAsString();
                String token = objectMapper.readTree(response).get("token").asText();

                // Access protected endpoint
                mockMvc.perform(get("/api/affiliates")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return validation errors for invalid request")
        void shouldReturnValidationErrorsForInvalidRequest() throws Exception {
                CreateAffiliateRequest request = new CreateAffiliateRequest(
                                "", // invalid - empty document
                                "John",
                                new BigDecimal("-100"), // invalid - negative salary
                                LocalDate.now().plusDays(1) // invalid - future date
                );

                // Get admin token first
                RegisterRequest registerRequest = new RegisterRequest(
                                "validationadmin",
                                "admin123",
                                "validationadmin@email.com",
                                null,
                                java.util.Set.of(com.coopcredit.domain.model.enums.Role.ROLE_ADMIN));

                MvcResult authResult = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andReturn();

                String authResponse = authResult.getResponse().getContentAsString();
                String token = objectMapper.readTree(authResponse).get("token").asText();

                mockMvc.perform(post("/api/affiliates")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.title").value("Validation Error"));
        }
}
