package com.dtsaas.backend.reports.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryRevenueResponse(
        UUID categoryId,
        String categoryName,
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal totalProfit,
        long quantity) {
}
