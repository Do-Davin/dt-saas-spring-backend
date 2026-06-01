package com.dtsaas.backend.businesses.dto;

import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;

import java.time.Instant;
import java.util.UUID;

public record BusinessResponse(
        UUID id,
        String name,
        String nameKm,
        String slug,
        BusinessType type,
        CatalogMode catalogMode,
        Instant createdAt,
        Instant updatedAt) {

    public static BusinessResponse from(Business business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getNameKm(),
                business.getSlug(),
                business.getType(),
                business.getCatalogMode(),
                business.getCreatedAt(),
                business.getUpdatedAt());
    }
}
