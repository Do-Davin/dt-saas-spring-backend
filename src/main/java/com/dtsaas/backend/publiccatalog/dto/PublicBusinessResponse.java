package com.dtsaas.backend.publiccatalog.dto;

import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;

import java.util.UUID;

public record PublicBusinessResponse(
        UUID id,
        String slug,
        String name,
        String nameKm,
        BusinessType businessType,
        CatalogMode catalogMode) {

    public static PublicBusinessResponse from(Business business) {
        return new PublicBusinessResponse(
                business.getId(),
                business.getSlug(),
                business.getName(),
                business.getNameKm(),
                business.getType(),
                business.getCatalogMode());
    }
}
