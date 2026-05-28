package com.mall.productcatalog.service;

import com.mall.productcatalog.dto.CategoryRequest;
import com.mall.productcatalog.dto.SubCategoryRequest;
import com.mall.productcatalog.entity.Category;
import com.mall.productcatalog.entity.SubCategory;
import com.mall.productcatalog.exception.DuplicateResourceException;
import com.mall.productcatalog.exception.ResourceNotFoundException;
import com.mall.productcatalog.repository.CategoryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(String categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        categoryRepository.delete(category);
    }

    public SubCategory createSubCategory(SubCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        boolean exists = category.getSubCategories().stream()
                .anyMatch(sub -> sub.getName().equalsIgnoreCase(request.getName()));

        if (exists) {
            throw new DuplicateResourceException("Subcategory '" + request.getName() + "' already exists in category '" + category.getName() + "'");
        }

        SubCategory subCategory = SubCategory.builder()
                .id(new ObjectId().toString())
                .name(request.getName())
                .build();

        category.getSubCategories().add(subCategory);
        categoryRepository.save(category);
        return subCategory;
    }

    public List<SubCategory> getSubCategoriesByCategoryId(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        return category.getSubCategories();
    }

    public SubCategory updateSubCategory(String subCategoryId, SubCategoryRequest request) {
        Category parentCategory = categoryRepository.findBySubCategoriesId(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + subCategoryId));

        SubCategory targetSub = parentCategory.getSubCategories().stream()
                .filter(sub -> sub.getId().equals(subCategoryId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + subCategoryId));

        // If shifting categories or changing name
        if (!parentCategory.getId().equals(request.getCategoryId())) {
            // Remove from current parent
            parentCategory.getSubCategories().remove(targetSub);
            categoryRepository.save(parentCategory);

            // Fetch new parent and add
            Category newParent = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target parent category not found with ID: " + request.getCategoryId()));

            boolean exists = newParent.getSubCategories().stream()
                    .anyMatch(sub -> sub.getName().equalsIgnoreCase(request.getName()));
            if (exists) {
                // Revert
                parentCategory.getSubCategories().add(targetSub);
                categoryRepository.save(parentCategory);
                throw new DuplicateResourceException("Subcategory '" + request.getName() + "' already exists in target category '" + newParent.getName() + "'");
            }

            targetSub.setName(request.getName());
            newParent.getSubCategories().add(targetSub);
            categoryRepository.save(newParent);
        } else {
            // Same parent, just check name
            boolean exists = parentCategory.getSubCategories().stream()
                    .anyMatch(sub -> !sub.getId().equals(subCategoryId) && sub.getName().equalsIgnoreCase(request.getName()));
            if (exists) {
                throw new DuplicateResourceException("Subcategory '" + request.getName() + "' already exists in category '" + parentCategory.getName() + "'");
            }
            targetSub.setName(request.getName());
            categoryRepository.save(parentCategory);
        }

        return targetSub;
    }

    public void deleteSubCategory(String subCategoryId) {
        Category parentCategory = categoryRepository.findBySubCategoriesId(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + subCategoryId));

        parentCategory.getSubCategories().removeIf(sub -> sub.getId().equals(subCategoryId));
        categoryRepository.save(parentCategory);
    }
}
