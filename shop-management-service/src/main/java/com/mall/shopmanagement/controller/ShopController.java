package com.mall.shopmanagement.controller;

import com.mall.shopmanagement.dto.ShopRequest;
import com.mall.shopmanagement.dto.ShopResponse;
import com.mall.shopmanagement.dto.UserPrincipal;
import com.mall.shopmanagement.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/request")
    public ResponseEntity<ShopResponse> requestShop(
            @Valid @RequestBody ShopRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ShopResponse response = shopService.requestShop(request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShopResponse>> getAllPendingRequests() {
        return ResponseEntity.ok(shopService.getAllPendingRequests());
    }

    @PutMapping("/{shopId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopResponse> approveShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.approveShop(shopId));
    }

    @PutMapping("/{shopId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopResponse> rejectShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.rejectShop(shopId));
    }

    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> closeShop(
            @PathVariable Long shopId,
            @AuthenticationPrincipal UserPrincipal principal) {
        shopService.closeShop(shopId, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-shops")
    public ResponseEntity<List<ShopResponse>> getMyShops(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(shopService.getShopsByOwner(principal.getId()));
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponse> getShopById(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.getShopById(shopId));
    }
}
