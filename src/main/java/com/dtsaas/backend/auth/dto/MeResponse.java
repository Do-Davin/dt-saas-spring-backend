package com.dtsaas.backend.auth.dto;

import com.dtsaas.backend.common.security.AuthenticatedOwner;

import java.util.UUID;

public record MeResponse(UUID id, String email, String name, String role) {

    public static MeResponse from(AuthenticatedOwner owner) {
        return new MeResponse(owner.id(), owner.email(), owner.name(), owner.role());
    }
}
