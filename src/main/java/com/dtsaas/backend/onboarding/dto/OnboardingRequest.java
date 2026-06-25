package com.dtsaas.backend.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OnboardingRequest(
        String plan,
        int extraItemPacks,
        int extraUserPacks,
        @NotBlank String businessName,
        String businessNameKm,
        String businessType,
        List<CategorySeedItem> categories,
        @NotBlank String fullName,
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password,
        String phoneNumber
) {
    public record CategorySeedItem(String name, List<String> products) {}
}
