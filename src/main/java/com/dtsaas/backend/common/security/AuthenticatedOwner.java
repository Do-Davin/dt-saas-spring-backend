package com.dtsaas.backend.common.security;

import java.util.UUID;

public record AuthenticatedOwner(UUID id, String email, String username, String name, String role) {
}
