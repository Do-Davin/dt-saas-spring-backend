package com.dtsaas.backend.products.dto;

import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.entity.UnitOfMeasure;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        UUID categoryId,
        String name,
        String nameKm,
        String description,
        String descriptionKm,
        BigDecimal purchasePrice,
        BigDecimal salesPrice,
        BigDecimal discount,
        PricingType pricingType,
        String label,
        UnitOfMeasure uom,
        Map<String, Object> toppings,
        Map<String, Object> ingredients,
        boolean isAvailable,
        boolean isVisible,
        Instant createdAt,
        Instant updatedAt,
        ProductPrimaryImageResponse primaryImage) {

    public static ProductResponse from(Product product, ProductPrimaryImageResponse primaryImage) {
        return new ProductResponse(
                product.getId(),
                product.getBusiness().getId(),
                product.getBranch() != null ? product.getBranch().getId() : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getName(),
                product.getNameKm(),
                product.getDescription(),
                product.getDescriptionKm(),
                product.getPurchasePrice(),
                product.getSalesPrice(),
                product.getDiscount(),
                product.getPricingType(),
                product.getLabel(),
                product.getUom(),
                product.getToppings(),
                product.getIngredients(),
                product.isAvailable(),
                product.isVisible(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                primaryImage);
    }
}
