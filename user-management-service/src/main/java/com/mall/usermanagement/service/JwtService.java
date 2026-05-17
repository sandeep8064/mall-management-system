package com.mall.usermanagement.service;

import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for JWT token operations including
 * generation, validation, and blacklisting (for logout).
 */
@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    /** In-memory token blacklist for logout support (MVP approach). */
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generate a JWT token for the given user.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        return jwtUtil.generateToken(user.getEmail(), claims);
    }

    /**
     * Validate a token — checks signature, expiry, subject match, and blacklist.
     */
    public boolean validateToken(String token, String email) {
        return !isBlacklisted(token) && jwtUtil.validateToken(token, email);
    }

    /**
     * Extract the email (subject) from the token.
     */
    public String extractEmail(String token) {
        return jwtUtil.extractSubject(token);
    }

    /**
     * Extract the user role from the token claims.
     */
    public String extractRole(String token) {
        return jwtUtil.extractClaims(token).get("role", String.class);
    }

    /**
     * Extract the user ID from the token claims.
     */
    public Long extractUserId(String token) {
        return jwtUtil.extractClaims(token).get("userId", Long.class);
    }

    /**
     * Blacklist a token (used on logout).
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Check if a token has been blacklisted.
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
