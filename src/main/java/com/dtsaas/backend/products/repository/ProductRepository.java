package com.dtsaas.backend.products.repository;

import com.dtsaas.backend.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByIdAndBusinessIdAndDeletedAtIsNull(UUID id, UUID businessId);

    Optional<Product> findByIdAndBusinessIdAndDeletedAtIsNullAndIsVisibleTrue(UUID id, UUID businessId);

    List<Product> findAllByBusinessIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID businessId);

    boolean existsByBusinessId(UUID businessId);
}
