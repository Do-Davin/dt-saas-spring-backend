package com.dtsaas.backend.sales.dto;

import com.dtsaas.backend.sales.entity.Sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        Instant saleDate,
        BigDecimal totalAmount,
        BigDecimal totalCost,
        BigDecimal profit,
        String note,
        List<SaleItemResponse> items,
        Instant createdAt,
        Instant updatedAt) {

    public static SaleResponse from(Sale sale) {
        List<SaleItemResponse> items = sale.getItems().stream()
                .map(SaleItemResponse::from)
                .toList();
        return new SaleResponse(
                sale.getId(),
                sale.getBusiness().getId(),
                sale.getBranch() != null ? sale.getBranch().getId() : null,
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getTotalCost(),
                sale.getProfit(),
                sale.getNote(),
                items,
                sale.getCreatedAt(),
                sale.getUpdatedAt());
    }
}
