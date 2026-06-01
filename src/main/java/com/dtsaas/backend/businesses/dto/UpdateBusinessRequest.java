package com.dtsaas.backend.businesses.dto;

import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateBusinessRequest(
        @Size(min = 1) String name,
        String nameKm,
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase letters, numbers, and hyphens only")
        String slug,
        BusinessType type,
        CatalogMode catalogMode) {
}
