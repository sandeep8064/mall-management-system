package com.mall.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.usermanagement.config.JwtAuthenticationFilter;
import com.mall.usermanagement.config.SecurityConfig;
import com.mall.usermanagement.dto.*;
import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.enums.Role;
import com.mall.usermanagement.service.JwtService;
import com.mall.usermanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController using @WebMvcTest.
 * Tests HTTP layer: request mapping, validation, security, and response serialization.
 */
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtService jwtService;

    private User createTestUser(Role role) {
        return User.builder().id(1L).name("John").email("john@example.com")
                .password("enc").role(role).build();
    }

    private UsernamePasswordAuthenticationToken authAs(Role role) {
        User user = createTestUser(role);
        return new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    }

    // ---- Registration ----

    @Test
    @DisplayName("POST /register — 201 on valid request")
    void register_Valid_Returns201() throws Exception {
        var req = RegisterRequest.builder().name("John").email("john@example.com").password("pass123").build();
        var res = AuthResponse.builder().token("t").email("john@example.com").role("CUSTOMER").message("Registration successful").build();
        when(userService.register(any(RegisterRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("t"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("POST /register — 400 on missing name")
    void register_MissingName_Returns400() throws Exception {
        var req = RegisterRequest.builder().email("j@e.com").password("pass123").build();

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register — 400 on invalid email")
    void register_BadEmail_Returns400() throws Exception {
        var req = RegisterRequest.builder().name("John").email("not-an-email").password("pass123").build();

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ---- Login ----

    @Test
    @DisplayName("POST /login — 200 on valid credentials")
    void login_Valid_Returns200() throws Exception {
        var req = LoginRequest.builder().email("john@example.com").password("pass").build();
        var res = AuthResponse.builder().token("t").email("john@example.com").role("CUSTOMER").message("Login successful").build();
        when(userService.login(any(LoginRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("t"));
    }

    // ---- Logout ----

    @Test
    @DisplayName("POST /logout — 200 for authenticated user")
    void logout_Authenticated_Returns200() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .header("Authorization", "Bearer some-token")
                        .with(authentication(authAs(Role.CUSTOMER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
        verify(userService).logout("some-token");
    }

    // ---- Profile ----

    @Test
    @DisplayName("GET /profile — 200 for authenticated user")
    void getProfile_Authenticated_Returns200() throws Exception {
        var profile = UserProfileResponse.builder().id(1L).name("John")
                .email("john@example.com").role(Role.CUSTOMER).build();
        when(userService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/users/profile")
                        .with(authentication(authAs(Role.CUSTOMER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DisplayName("GET /profile — 401 without auth")
    void getProfile_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /profile — 200 updates profile")
    void updateProfile_Valid_Returns200() throws Exception {
        var req = UpdateProfileRequest.builder().phone("111").build();
        var profile = UserProfileResponse.builder().id(1L).name("John")
                .email("john@example.com").phone("111").role(Role.CUSTOMER).build();
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(profile);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(authentication(authAs(Role.CUSTOMER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("111"));
    }

    // ---- Admin Endpoints ----

    @Test
    @DisplayName("GET /api/users — 200 for ADMIN")
    void getAllUsers_Admin_Returns200() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                UserProfileResponse.builder().id(1L).name("John").role(Role.CUSTOMER).build()));

        mockMvc.perform(get("/api/users")
                        .with(authentication(authAs(Role.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    @DisplayName("GET /api/users — 403 for non-ADMIN")
    void getAllUsers_Customer_Returns403() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(authentication(authAs(Role.CUSTOMER))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /{userId}/role — 200 for ADMIN")
    void updateRole_Admin_Returns200() throws Exception {
        var req = RoleUpdateRequest.builder().role("SHOP_OWNER").build();
        var profile = UserProfileResponse.builder().id(2L).name("Jane").role(Role.SHOP_OWNER).build();
        when(userService.updateRole(eq(2L), any(RoleUpdateRequest.class))).thenReturn(profile);

        mockMvc.perform(put("/api/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(authentication(authAs(Role.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("SHOP_OWNER"));
    }

    @Test
    @DisplayName("PUT /{userId}/role — 403 for non-ADMIN")
    void updateRole_Customer_Returns403() throws Exception {
        var req = RoleUpdateRequest.builder().role("ADMIN").build();

        mockMvc.perform(put("/api/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(authentication(authAs(Role.CUSTOMER))))
                .andExpect(status().isForbidden());
    }
}
