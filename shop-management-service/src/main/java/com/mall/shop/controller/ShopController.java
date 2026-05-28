package com.mall.shop.controller;

import com.mall.shop.dto.ShopRequest;
import com.mall.shop.dto.ShopResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @PostMapping
    public ResponseEntity<ShopResponse> createShop(@RequestBody ShopRequest request) {
        return new ResponseEntity<>(new ShopResponse(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShop(@PathVariable Long id) {
        return ResponseEntity.ok(new ShopResponse());
    }

    @GetMapping
    public ResponseEntity<List<ShopResponse>> getAllShops() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopResponse> updateShop(@PathVariable Long id, @RequestBody ShopRequest request) {
        return ResponseEntity.ok(new ShopResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
