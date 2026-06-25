package com.dtsaas.backend.businesses.dto;

import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.entity.BusinessType;
import com.dtsaas.backend.businesses.entity.CatalogMode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BusinessResponse(
        UUID id,
        String name,
        String nameKm,
        String slug,
        BusinessType type,
        CatalogMode catalogMode,
        String subscriptionStatus,
        Instant subscriptionStartDate,
        Instant subscriptionExpiresAt,
        String subscriptionPlan,
        Integer userLimit,
        Integer menuItemLimit,
        BigDecimal monthlyPrice,
        Instant createdAt,
        Instant updatedAt) {

    public static BusinessResponse from(Business business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getNameKm(),
                business.getSlug(),
                business.getType(),
                business.getCatalogMode(),
                business.getSubscriptionStatus(),
                business.getSubscriptionStartDate(),
                business.getSubscriptionExpiresAt(),
                business.getSubscriptionPlan(),
                business.getUserLimit(),
                business.getMenuItemLimit(),
                business.getMonthlyPrice(),
                business.getCreatedAt(),
                business.getUpdatedAt());
    }
}
