package com.mall.productcatalog.service;

import com.mall.productcatalog.dto.ProductRequest;
import com.mall.productcatalog.dto.ProductResponse;
import com.mall.productcatalog.entity.Category;
import com.mall.productcatalog.entity.Product;
import com.mall.productcatalog.entity.SubCategory;
import com.mall.productcatalog.enums.ProductStatus;
import com.mall.productcatalog.exception.ResourceNotFoundException;
import com.mall.productcatalog.repository.CategoryRepository;
import com.mall.productcatalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        SubCategory subCategory = category.getSubCategories().stream()
                .filter(sub -> sub.getId().equals(request.getSubcategoryId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + request.getSubcategoryId()));

        Product product = Product.builder()
                .shopId(request.getShopId())
                .categoryId(category.getId())
                .subcategoryId(subCategory.getId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .status(ProductStatus.ACTIVE)
                .attributes(request.getAttributes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct, category, subCategory);
    }

    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        Category category = categoryRepository.findById(product.getCategoryId())
                .orElse(null);

        SubCategory subCategory = null;
        if (category != null) {
            subCategory = category.getSubCategories().stream()
                    .filter(sub -> sub.getId().equals(product.getSubcategoryId()))
                    .findFirst()
                    .orElse(null);
        }

        return mapToResponse(product, category, subCategory);
    }

    public ProductResponse updateProduct(String productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        SubCategory subCategory = category.getSubCategories().stream()
                .filter(sub -> sub.getId().equals(request.getSubcategoryId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + request.getSubcategoryId()));

        product.setShopId(request.getShopId());
        product.setCategoryId(category.getId());
        product.setSubcategoryId(subCategory.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setAttributes(request.getAttributes());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct, category, subCategory);
    }

    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        product.setStatus(ProductStatus.CANCELLED);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public List<ProductResponse> browseProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::enrichProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByShop(Long shopId) {
        return productRepository.findByShopIdAndStatus(shopId, ProductStatus.ACTIVE).stream()
                .map(this::enrichProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProductsByKeyword(keyword, ProductStatus.ACTIVE).stream()
                .map(this::enrichProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse enrichProductResponse(Product product) {
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        SubCategory subCategory = null;
        if (category != null) {
            subCategory = category.getSubCategories().stream()
                    .filter(sub -> sub.getId().equals(product.getSubcategoryId()))
                    .findFirst()
                    .orElse(null);
        }
        return mapToResponse(product, category, subCategory);
    }

    private ProductResponse mapToResponse(Product product, Category category, SubCategory subCategory) {
        return ProductResponse.builder()
                .id(product.getId())
                .shopId(product.getShopId())
                .categoryId(product.getCategoryId())
                .categoryName(category != null ? category.getName() : "Unknown")
                .subcategoryId(product.getSubcategoryId())
                .subcategoryName(subCategory != null ? subCategory.getName() : "Unknown")
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus().name())
                .attributes(product.getAttributes())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
