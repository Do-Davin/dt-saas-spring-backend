package com.dtsaas.backend.businesses.dto;

import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBusinessRequest(
        @NotBlank @Size(min = 1) String name,
        String nameKm,
        @NotBlank
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase letters, numbers, and hyphens only")
        String slug,
        @NotNull BusinessType type,
        CatalogMode catalogMode) {
}
