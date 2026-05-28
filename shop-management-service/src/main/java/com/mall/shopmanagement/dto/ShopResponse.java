package com.mall.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
