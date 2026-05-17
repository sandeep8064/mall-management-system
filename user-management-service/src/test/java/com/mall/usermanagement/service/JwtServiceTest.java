package com.mall.usermanagement.service;

import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.enums.Role;
import com.mall.usermanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtService.
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtUtil);
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("Should generate token for user")
    void generateToken_ValidUser_ReturnsToken() {
        when(jwtUtil.generateToken(eq("john@example.com"), any(Map.class)))
                .thenReturn("mock-jwt-token");

        String token = jwtService.generateToken(testUser);

        assertThat(token).isEqualTo("mock-jwt-token");
        verify(jwtUtil).generateToken(eq("john@example.com"), any(Map.class));
    }

    @Test
    @DisplayName("Should validate non-blacklisted token")
    void validateToken_ValidNonBlacklisted_ReturnsTrue() {
        when(jwtUtil.validateToken("valid-token", "john@example.com")).thenReturn(true);

        boolean result = jwtService.validateToken("valid-token", "john@example.com");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject blacklisted token")
    void validateToken_BlacklistedToken_ReturnsFalse() {
        jwtService.blacklistToken("blacklisted-token");

        boolean result = jwtService.validateToken("blacklisted-token", "john@example.com");

        assertThat(result).isFalse();
        verify(jwtUtil, never()).validateToken(any(), any());
    }

    @Test
    @DisplayName("Should extract email from token")
    void extractEmail_ValidToken_ReturnsEmail() {
        when(jwtUtil.extractSubject("some-token")).thenReturn("john@example.com");

        String email = jwtService.extractEmail("some-token");

        assertThat(email).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should track blacklisted tokens")
    void isBlacklisted_AfterBlacklist_ReturnsTrue() {
        assertThat(jwtService.isBlacklisted("token-1")).isFalse();

        jwtService.blacklistToken("token-1");

        assertThat(jwtService.isBlacklisted("token-1")).isTrue();
    }

    @Test
    @DisplayName("Non-blacklisted token should not be flagged")
    void isBlacklisted_NeverBlacklisted_ReturnsFalse() {
        assertThat(jwtService.isBlacklisted("fresh-token")).isFalse();
    }
}
