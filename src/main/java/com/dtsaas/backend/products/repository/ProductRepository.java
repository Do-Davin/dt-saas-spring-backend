package com.dtsaas.backend.products.repository;

import com.dtsaas.backend.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndBusinessIdAndDeletedAtIsNull(UUID id, UUID businessId);

    List<Product> findAllByBusinessIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID businessId);
}
