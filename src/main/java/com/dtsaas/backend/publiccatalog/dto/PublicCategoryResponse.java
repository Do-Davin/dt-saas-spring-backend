package com.dtsaas.backend.publiccatalog.dto;

import com.dtsaas.backend.categories.entity.Category;

import java.util.UUID;

public record PublicCategoryResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        String name,
        String nameKm,
        int position,
        boolean isActive) {

    public static PublicCategoryResponse from(Category category) {
        return new PublicCategoryResponse(
                category.getId(),
                category.getBusiness().getId(),
                category.getBranch() != null ? category.getBranch().getId() : null,
                category.getName(),
                category.getNameKm(),
                category.getPosition(),
                category.isActive());
    }
}
