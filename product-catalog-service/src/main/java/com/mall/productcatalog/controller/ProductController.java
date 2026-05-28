package com.mall.productcatalog.controller;

import com.mall.productcatalog.dto.ProductRequest;
import com.mall.productcatalog.dto.ProductResponse;
import com.mall.productcatalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SHOP_OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> browseProducts() {
        return ResponseEntity.ok(productService.browseProducts());
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getProductsByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(productService.getProductsByShop(shopId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
