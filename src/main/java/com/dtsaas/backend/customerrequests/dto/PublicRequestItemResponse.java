package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.CustomerRequestItem;
import com.dtsaas.backend.products.entity.PricingType;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicRequestItemResponse(
        UUID id,
        String productNameSnapshot,
        BigDecimal salesPriceSnapshot,
        PricingType pricingTypeSnapshot,
        int quantity,
        String note) {

    public static PublicRequestItemResponse from(CustomerRequestItem item) {
        return new PublicRequestItemResponse(
                item.getId(),
                item.getProductNameSnapshot(),
                item.getSalesPriceSnapshot(),
                item.getPricingTypeSnapshot(),
                item.getQuantity(),
                item.getNote());
    }
}
