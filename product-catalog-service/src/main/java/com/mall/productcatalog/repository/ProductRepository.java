package com.mall.productcatalog.repository;

import com.mall.productcatalog.entity.Product;
import com.mall.productcatalog.enums.ProductStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByShopIdAndStatus(Long shopId, ProductStatus status);

    @Query("{'status': ?1, $or: [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    List<Product> searchProductsByKeyword(String keyword, ProductStatus status);
}
