package com.mall.usermanagement.dto;

import lombok.*;

/**
 * Request DTO for updating user profile (phone and shipping address only).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String phone;
    private String shippingAddress;
}
