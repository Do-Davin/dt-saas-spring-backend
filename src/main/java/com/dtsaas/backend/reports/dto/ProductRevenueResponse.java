package com.dtsaas.backend.reports.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRevenueResponse(
        UUID productId,
        String productName,
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal totalProfit,
        long quantity) {
}
