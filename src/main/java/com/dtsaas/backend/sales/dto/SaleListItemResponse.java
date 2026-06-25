package com.dtsaas.backend.sales.dto;

import com.dtsaas.backend.sales.entity.Sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaleListItemResponse(
        UUID id,
        UUID branchId,
        Instant saleDate,
        BigDecimal totalAmount,
        BigDecimal totalCost,
        BigDecimal profit,
        int itemCount,
        String note,
        Instant createdAt) {

    public static SaleListItemResponse from(Sale sale) {
        return new SaleListItemResponse(
                sale.getId(),
                sale.getBranch() != null ? sale.getBranch().getId() : null,
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getTotalCost(),
                sale.getProfit(),
                sale.getItemCount(),
                sale.getNote(),
                sale.getCreatedAt());
    }
}
