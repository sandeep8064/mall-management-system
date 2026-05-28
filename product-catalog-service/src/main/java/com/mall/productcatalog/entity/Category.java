package com.mall.productcatalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private String id;

    private String name;

    private String description;

    @Builder.Default
    private List<SubCategory> subCategories = new ArrayList<>();
}
