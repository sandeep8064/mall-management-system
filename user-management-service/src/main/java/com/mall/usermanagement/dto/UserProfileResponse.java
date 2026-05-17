package com.mall.usermanagement.dto;

import com.mall.usermanagement.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for user profile information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String shippingAddress;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
