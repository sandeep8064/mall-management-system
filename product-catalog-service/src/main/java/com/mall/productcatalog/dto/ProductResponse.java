package com.mall.productcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private Long shopId;
    private String categoryId;
    private String categoryName;
    private String subcategoryId;
    private String subcategoryName;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String status;
    private String attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
