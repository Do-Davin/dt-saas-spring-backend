package com.dtsaas.backend.publiccatalog.dto;

import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.entity.UnitOfMeasure;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PublicProductResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        UUID categoryId,
        String name,
        String nameKm,
        String description,
        String descriptionKm,
        BigDecimal salesPrice,
        BigDecimal discount,
        PricingType pricingType,
        UnitOfMeasure uom,
        String label,
        Map<String, Object> toppings,
        Map<String, Object> ingredients,
        boolean isAvailable,
        boolean isVisible,
        PublicProductImageResponse primaryImage,
        // null on list responses; full sorted list on detail responses
        @JsonInclude(JsonInclude.Include.NON_NULL) List<PublicProductImageResponse> images) {

    public static PublicProductResponse from(Product product,
            PublicProductImageResponse primaryImage,
            List<PublicProductImageResponse> images) {
        return new PublicProductResponse(
                product.getId(),
                product.getBusiness().getId(),
                product.getBranch() != null ? product.getBranch().getId() : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getName(),
                product.getNameKm(),
                product.getDescription(),
                product.getDescriptionKm(),
                product.getSalesPrice(),
                product.getDiscount(),
                product.getPricingType(),
                product.getUom(),
                product.getLabel(),
                product.getToppings(),
                product.getIngredients(),
                product.isAvailable(),
                product.isVisible(),
                primaryImage,
                images);
    }
}
