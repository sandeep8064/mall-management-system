package com.mall.productcatalog.repository;

import com.mall.productcatalog.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    boolean existsByName(String name);
    Optional<Category> findBySubCategoriesId(String subCategoryId);
}
