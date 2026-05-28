package com.mall.productcatalog.controller;

import com.mall.productcatalog.dto.CategoryRequest;
import com.mall.productcatalog.dto.SubCategoryRequest;
import com.mall.productcatalog.entity.Category;
import com.mall.productcatalog.entity.SubCategory;
import com.mall.productcatalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ================= CATEGORY ENDPOINTS =================

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    // ================= SUBCATEGORY ENDPOINTS =================

    @PostMapping("/subcategories")
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<SubCategory> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        SubCategory subCategory = categoryService.createSubCategory(request);
        return new ResponseEntity<>(subCategory, HttpStatus.CREATED);
    }

    @GetMapping("/subcategories/{categoryId}")
    public ResponseEntity<List<SubCategory>> getSubCategoriesByCategoryId(@PathVariable String categoryId) {
        return ResponseEntity.ok(categoryService.getSubCategoriesByCategoryId(categoryId));
    }

    @PutMapping("/subcategories/{subCategoryId}")
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<SubCategory> updateSubCategory(
            @PathVariable String subCategoryId,
            @Valid @RequestBody SubCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateSubCategory(subCategoryId, request));
    }

    @DeleteMapping("/subcategories/{subCategoryId}")
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable String subCategoryId) {
        categoryService.deleteSubCategory(subCategoryId);
        return ResponseEntity.noContent().build();
    }
}
