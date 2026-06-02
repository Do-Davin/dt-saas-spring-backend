package com.dtsaas.backend.categories.repository;

import com.dtsaas.backend.categories.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByIdAndBusinessId(UUID id, UUID businessId);

    List<Category> findAllByBusinessIdOrderByPositionAscCreatedAtDesc(UUID businessId);

    List<Category> findAllByBusinessIdAndBranchIdOrderByPositionAscCreatedAtDesc(UUID businessId, UUID branchId);

    List<Category> findAllByBusinessIdAndIsActiveTrueOrderByPositionAscNameAsc(UUID businessId);

    List<Category> findAllByBusinessIdAndBranchIdAndIsActiveTrueOrderByPositionAscNameAsc(UUID businessId, UUID branchId);

    boolean existsByBusinessId(UUID businessId);

    boolean existsByBranchId(UUID branchId);
}
