package com.mall.productcatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryRequest {

    @NotBlank(message = "Parent Category ID is required")
    private String categoryId;

    @NotBlank(message = "Subcategory name is required")
    @Size(min = 2, max = 50, message = "Subcategory name must be between 2 and 50 characters")
    private String name;
}
