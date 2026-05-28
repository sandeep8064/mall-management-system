package com.mall.productcatalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Subcategory ID is required")
    private String subcategoryId;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock level is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String attributes; // Key-value details in flexible format
}
