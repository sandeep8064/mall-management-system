package com.mall.shopmanagement.repository;

import com.mall.shopmanagement.entity.Shop;
import com.mall.shopmanagement.enums.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByOwnerId(Long ownerId);
    List<Shop> findByStatus(ShopStatus status);
    boolean existsByName(String name);
}
