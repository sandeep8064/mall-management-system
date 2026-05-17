package com.mall.usermanagement.dto;

import lombok.*;

/**
 * Response DTO returned after successful authentication.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String email;
    private String role;
    private String message;
}
