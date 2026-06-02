package com.dtsaas.backend.productimages.repository;

import com.dtsaas.backend.productimages.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findAllByProductIdOrderByPositionAscCreatedAtAsc(UUID productId);

    Optional<ProductImage> findByIdAndProductId(UUID id, UUID productId);

    long countByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    Optional<ProductImage> findFirstByProductIdOrderByPositionAscCreatedAtAsc(UUID productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId AND pi.isPrimary = true")
    void demotePrimaries(@Param("productId") UUID productId);
}
