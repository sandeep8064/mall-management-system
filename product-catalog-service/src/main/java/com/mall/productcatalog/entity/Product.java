package com.mall.productcatalog.entity;

import com.mall.productcatalog.enums.ProductStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id;

    private Long shopId;

    private String categoryId;

    private String subcategoryId;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private ProductStatus status;

    private String attributes; // Key-value details in flexible JSON-like formats

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
