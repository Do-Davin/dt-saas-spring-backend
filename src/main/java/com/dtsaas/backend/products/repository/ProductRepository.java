package com.dtsaas.backend.products.repository;

import com.dtsaas.backend.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByIdAndBusinessIdAndDeletedAtIsNull(UUID id, UUID businessId);

    Optional<Product> findByIdAndBusinessIdAndDeletedAtIsNullAndIsVisibleTrue(UUID id, UUID businessId);

    List<Product> findAllByBusinessIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID businessId);

    boolean existsByBusinessId(UUID businessId);

    @Query("""
            SELECT p FROM Product p
            WHERE p.business.id = :businessId
              AND p.deletedAt IS NULL
              AND (p.stockQuantity = 0
                   OR (p.lowStockThreshold IS NOT NULL
                       AND p.stockQuantity <= p.lowStockThreshold))
            ORDER BY p.stockQuantity ASC, p.name ASC
            """)
    List<Product> findStockAlerts(@Param("businessId") UUID businessId);
}
