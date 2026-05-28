package com.mall.shopmanagement.service;

import com.mall.shopmanagement.dto.ShopRequest;
import com.mall.shopmanagement.dto.ShopResponse;
import com.mall.shopmanagement.dto.UserPrincipal;
import com.mall.shopmanagement.entity.Shop;
import com.mall.shopmanagement.enums.ShopStatus;
import com.mall.shopmanagement.exception.DuplicateResourceException;
import com.mall.shopmanagement.exception.ResourceNotFoundException;
import com.mall.shopmanagement.exception.UnauthorizedException;
import com.mall.shopmanagement.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public ShopResponse requestShop(ShopRequest request, UserPrincipal principal) {
        if (shopRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Shop with name '" + request.getName() + "' already exists");
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .ownerId(principal.getId())
                .status(ShopStatus.PENDING)
                .build();

        Shop savedShop = shopRepository.save(shop);
        return mapToResponse(savedShop);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getAllPendingRequests() {
        return shopRepository.findByStatus(ShopStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ShopResponse approveShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));

        if (shop.getStatus() == ShopStatus.ACTIVE) {
            throw new IllegalArgumentException("Shop is already active");
        }

        shop.setStatus(ShopStatus.ACTIVE);
        Shop updatedShop = shopRepository.save(shop);
        return mapToResponse(updatedShop);
    }

    public ShopResponse rejectShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));

        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new IllegalArgumentException("Only pending shop requests can be rejected");
        }

        shop.setStatus(ShopStatus.REJECTED);
        Shop updatedShop = shopRepository.save(shop);
        return mapToResponse(updatedShop);
    }

    public void closeShop(Long shopId, UserPrincipal principal) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));

        boolean isAdmin = "ADMIN".equals(principal.getRole());
        boolean isOwner = shop.getOwnerId().equals(principal.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("You are not authorized to close this shop");
        }

        shop.setStatus(ShopStatus.CANCELLED);
        shopRepository.save(shop);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByOwner(Long ownerId) {
        return shopRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        return mapToResponse(shop);
    }

    private ShopResponse mapToResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .status(shop.getStatus().name())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}
