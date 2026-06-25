package com.dtsaas.backend.onboarding.dto;

import com.dtsaas.backend.auth.dto.AuthOwnerResponse;

import java.util.UUID;

public record OnboardingResponse(
        String accessToken,
        AuthOwnerResponse owner,
        UUID businessId,
        String businessSlug
) {}
