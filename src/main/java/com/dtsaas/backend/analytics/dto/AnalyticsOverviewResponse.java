package com.dtsaas.backend.analytics.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AnalyticsOverviewResponse(
        String period,
        BigDecimal income,
        BigDecimal cost,
        BigDecimal profit,
        long saleCount,
        List<TopProduct> topSellingProducts,
        List<CategoryShare> salesByCategory,
        List<StockAlert> stockAlerts) {

    public record TopProduct(
            UUID productId,
            String name,
            long quantity,
            BigDecimal revenue) {}

    public record CategoryShare(
            UUID categoryId,
            String name,
            BigDecimal revenue,
            BigDecimal percentage) {}

    public record StockAlert(
            UUID productId,
            String name,
            int stockQuantity,
            Integer lowStockThreshold) {}
}
