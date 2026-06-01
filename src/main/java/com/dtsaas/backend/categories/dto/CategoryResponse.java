package com.dtsaas.backend.categories.dto;

import com.dtsaas.backend.categories.entity.Category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        String name,
        String nameKm,
        int position,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getBusiness().getId(),
                category.getBranch() != null ? category.getBranch().getId() : null,
                category.getName(),
                category.getNameKm(),
                category.getPosition(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
