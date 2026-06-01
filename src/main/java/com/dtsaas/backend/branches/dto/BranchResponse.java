package com.dtsaas.backend.branches.dto;

import com.dtsaas.backend.branches.entity.Branch;

import java.time.Instant;
import java.util.UUID;

public record BranchResponse(
        UUID id,
        UUID businessId,
        String name,
        String nameKm,
        String slug,
        Instant createdAt,
        Instant updatedAt) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getBusiness().getId(),
                branch.getName(),
                branch.getNameKm(),
                branch.getSlug(),
                branch.getCreatedAt(),
                branch.getUpdatedAt());
    }
}
