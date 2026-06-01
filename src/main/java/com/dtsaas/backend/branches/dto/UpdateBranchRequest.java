package com.dtsaas.backend.branches.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateBranchRequest(
        @Size(min = 1) String name,
        String nameKm,
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase letters, numbers, and hyphens only")
        String slug) {
}
