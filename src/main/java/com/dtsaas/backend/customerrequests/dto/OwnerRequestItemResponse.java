package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.CustomerRequestItem;
import com.dtsaas.backend.products.entity.PricingType;

import java.math.BigDecimal;
import java.util.UUID;

public record OwnerRequestItemResponse(
        UUID id,
        UUID productId,
        String productNameSnapshot,
        BigDecimal salesPriceSnapshot,
        PricingType pricingTypeSnapshot,
        int quantity,
        String note) {

    public static OwnerRequestItemResponse from(CustomerRequestItem item) {
        return new OwnerRequestItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProductNameSnapshot(),
                item.getSalesPriceSnapshot(),
                item.getPricingTypeSnapshot(),
                item.getQuantity(),
                item.getNote());
    }
}
