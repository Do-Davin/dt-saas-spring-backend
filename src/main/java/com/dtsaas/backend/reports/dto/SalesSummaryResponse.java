package com.dtsaas.backend.reports.dto;

import java.math.BigDecimal;

public record SalesSummaryResponse(
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal totalProfit,
        long saleCount) {
}
