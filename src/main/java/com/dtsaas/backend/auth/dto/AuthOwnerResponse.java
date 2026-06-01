package com.dtsaas.backend.auth.dto;

import com.dtsaas.backend.auth.entity.Owner;

import java.time.Instant;
import java.util.UUID;

public record AuthOwnerResponse(
        UUID id,
        String email,
        String name,
        Instant createdAt,
        Instant updatedAt) {

    public static AuthOwnerResponse from(Owner owner) {
        return new AuthOwnerResponse(
                owner.getId(),
                owner.getEmail(),
                owner.getName(),
                owner.getCreatedAt(),
                owner.getUpdatedAt());
    }
}
