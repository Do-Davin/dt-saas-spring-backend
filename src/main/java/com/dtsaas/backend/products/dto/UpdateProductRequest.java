package com.dtsaas.backend.products.dto;

import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.UnitOfMeasure;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record UpdateProductRequest(
        @Size(min = 1) String name,
        String nameKm,
        String description,
        String descriptionKm,
        UUID branchId,
        UUID categoryId,
        BigDecimal purchasePrice,
        BigDecimal salesPrice,
        BigDecimal discount,
        PricingType pricingType,
        String label,
        UnitOfMeasure uom,
        Map<String, Object> toppings,
        Map<String, Object> ingredients,
        Boolean isAvailable,
        Boolean isVisible) {
}
