package com.dtsaas.backend.publiccatalog.dto;

import com.dtsaas.backend.branches.entity.Branch;

import java.util.UUID;

public record PublicBranchResponse(
        UUID id,
        UUID businessId,
        String slug,
        String name,
        String nameKm) {

    public static PublicBranchResponse from(Branch branch) {
        return new PublicBranchResponse(
                branch.getId(),
                branch.getBusiness().getId(),
                branch.getSlug(),
                branch.getName(),
                branch.getNameKm());
    }
}
