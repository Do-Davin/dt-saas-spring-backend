package com.dtsaas.backend.branches.repository;

import com.dtsaas.backend.branches.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    Optional<Branch> findByIdAndBusinessId(UUID id, UUID businessId);

    Optional<Branch> findByBusinessIdAndSlug(UUID businessId, String slug);

    List<Branch> findAllByBusinessIdOrderByCreatedAtDesc(UUID businessId);

    List<Branch> findAllByBusinessIdOrderByNameAsc(UUID businessId);

    boolean existsByBusinessIdAndSlug(UUID businessId, String slug);

    boolean existsByBusinessIdAndSlugAndIdNot(UUID businessId, String slug, UUID id);

    boolean existsByBusinessId(UUID businessId);
}
