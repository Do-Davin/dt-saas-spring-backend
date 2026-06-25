package com.dtsaas.backend.common.security;

import com.dtsaas.backend.common.exception.ApiException;

public final class RoleGuard {

    private RoleGuard() {}

    // Throws 403 if the authenticated owner does not hold the SUPER_ADMIN role.
    // Call as the first statement in any endpoint that must be SUPER_ADMIN-only.
    public static void requireSuperAdmin(AuthenticatedOwner owner) {
        if (!"SUPER_ADMIN".equals(owner.role())) {
            throw ApiException.forbidden("Access restricted to SUPER_ADMIN");
        }
    }
}
