package com.dtsaas.backend.auth.dto;

public record AuthResponse(AuthOwnerResponse owner, String accessToken) {
}
