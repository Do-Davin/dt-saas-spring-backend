package com.dtsaas.backend.categories.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCategoryRequest(
        @Size(min = 1) String name,
        String nameKm,
        UUID branchId,
        @Min(0) Integer position,
        Boolean isActive) {
}
