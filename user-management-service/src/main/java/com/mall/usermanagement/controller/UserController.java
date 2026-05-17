package com.mall.usermanagement.controller;

import com.mall.usermanagement.dto.*;
import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.service.JwtService;
import com.mall.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for User Management endpoints.
 * Implements all 7 endpoints from the requirements:
 * - POST /register, /login, /logout
 * - GET/PUT /profile
 * - GET / (admin), PUT /{userId}/role (admin)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user (customer or shop owner).
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login with email and password, returns JWT.
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout — invalidates the JWT token.
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        userService.logout(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Get current user's profile.
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        UserProfileResponse profile = userService.getProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Update current user's profile (phone, shipping address).
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {
        User currentUser = (User) authentication.getPrincipal();
        UserProfileResponse profile = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(profile);
    }

    /**
     * List all users — Admin only.
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update a user's role — Admin only.
     * PUT /api/users/{userId}/role
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleUpdateRequest request) {
        UserProfileResponse profile = userService.updateRole(userId, request);
        return ResponseEntity.ok(profile);
    }
}
