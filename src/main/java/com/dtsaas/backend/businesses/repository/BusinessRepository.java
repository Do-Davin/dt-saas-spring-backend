package com.dtsaas.backend.businesses.repository;

import com.dtsaas.backend.businesses.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID> {

    boolean existsBySlug(String slug);

    Optional<Business> findBySlug(String slug);

    Optional<Business> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Business> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    List<Business> findAllByOrderByCreatedAtDesc();
}
