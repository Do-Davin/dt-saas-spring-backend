package com.dtsaas.backend.sales.dto;

import com.dtsaas.backend.sales.entity.SaleItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaleItemResponse(
        UUID id,
        UUID productId,
        String productNameSnapshot,
        UUID categoryIdSnapshot,
        String categoryNameSnapshot,
        int quantity,
        BigDecimal unitSalesPrice,
        BigDecimal unitCostPrice,
        BigDecimal discountAmount,
        BigDecimal lineTotal,
        BigDecimal lineCost,
        Instant createdAt) {

    public static SaleItemResponse from(SaleItem item) {
        return new SaleItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProductNameSnapshot(),
                item.getCategoryIdSnapshot(),
                item.getCategoryNameSnapshot(),
                item.getQuantity(),
                item.getUnitSalesPrice(),
                item.getUnitCostPrice(),
                item.getDiscountAmount(),
                item.getLineTotal(),
                item.getLineCost(),
                item.getCreatedAt());
    }
}
