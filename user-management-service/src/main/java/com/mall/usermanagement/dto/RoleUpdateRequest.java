package com.mall.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for admin role assignment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateRequest {

    @NotBlank(message = "Role is required")
    private String role;
}
