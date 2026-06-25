package com.dtsaas.backend.auth.dto;

import com.dtsaas.backend.auth.entity.Owner;

import java.time.Instant;
import java.util.UUID;

public record AuthOwnerResponse(
        UUID id,
        String email,
        String username,
        String name,
        String role,
        Instant createdAt,
        Instant updatedAt) {

    public static AuthOwnerResponse from(Owner owner) {
        return new AuthOwnerResponse(
                owner.getId(),
                owner.getEmail(),
                owner.getUsername(),
                owner.getName(),
                owner.getRole(),
                owner.getCreatedAt(),
                owner.getUpdatedAt());
    }
}
