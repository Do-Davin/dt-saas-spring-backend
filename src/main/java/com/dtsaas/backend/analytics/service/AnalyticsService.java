package com.dtsaas.backend.analytics.service;

import com.dtsaas.backend.analytics.dto.AnalyticsOverviewResponse;
import com.dtsaas.backend.analytics.dto.AnalyticsPeriod;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import com.dtsaas.backend.sales.repository.SaleItemRepository;
import com.dtsaas.backend.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BusinessService businessService;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse overview(UUID businessId, UUID ownerId, AnalyticsPeriod period) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        Instant[] range = resolvePeriod(period);
        Instant from = range[0];
        Instant to   = range[1];

        // ── Summary ───────────────────────────────────────────────────────────
        SaleRepository.SalesSummaryRow summaryRow =
                saleRepository.findSalesSummary(businessId, from, to, null);

        BigDecimal income   = nvl(summaryRow.getTotalRevenue());
        BigDecimal cost     = nvl(summaryRow.getTotalCost());
        BigDecimal profit   = nvl(summaryRow.getTotalProfit());
        long saleCount      = summaryRow.getSaleCount() != null ? summaryRow.getSaleCount() : 0L;

        // ── Top 5 products by revenue ─────────────────────────────────────────
        List<AnalyticsOverviewResponse.TopProduct> topProducts = saleItemRepository
                .findRevenueByProduct(businessId, from, to, null)
                .stream()
                .limit(5)
                .map(r -> new AnalyticsOverviewResponse.TopProduct(
                        r.getProductId(),
                        r.getProductName(),
                        r.getQuantity() != null ? r.getQuantity() : 0L,
                        nvl(r.getTotalRevenue())))
                .toList();

        // ── Top 5 categories by revenue with percentage ───────────────────────
        List<AnalyticsOverviewResponse.CategoryShare> categoryShares = saleItemRepository
                .findRevenueByCategory(businessId, from, to, null)
                .stream()
                .limit(5)
                .map(r -> {
                    BigDecimal catRevenue = nvl(r.getTotalRevenue());
                    BigDecimal pct = income.compareTo(BigDecimal.ZERO) > 0
                            ? catRevenue.multiply(BigDecimal.valueOf(100))
                                    .divide(income, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new AnalyticsOverviewResponse.CategoryShare(
                            r.getCategoryId(),
                            r.getCategoryName() != null ? r.getCategoryName() : "Uncategorized",
                            catRevenue,
                            pct);
                })
                .toList();

        // ── Stock alerts ──────────────────────────────────────────────────────
        List<AnalyticsOverviewResponse.StockAlert> stockAlerts = productRepository
                .findStockAlerts(businessId)
                .stream()
                .map(p -> new AnalyticsOverviewResponse.StockAlert(
                        p.getId(),
                        p.getName(),
                        p.getStockQuantity(),
                        p.getLowStockThreshold()))
                .toList();

        return new AnalyticsOverviewResponse(
                period.name(), income, cost, profit, saleCount,
                topProducts, categoryShares, stockAlerts);
    }

    // ── Period resolution ─────────────────────────────────────────────────────

    private static Instant[] resolvePeriod(AnalyticsPeriod period) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return switch (period) {
            case THIS_WEEK -> {
                Instant weekStart = now.with(DayOfWeek.MONDAY)
                        .truncatedTo(ChronoUnit.DAYS)
                        .toInstant();
                yield new Instant[]{ weekStart, null };
            }
            case THIS_MONTH -> {
                Instant monthStart = now.withDayOfMonth(1)
                        .truncatedTo(ChronoUnit.DAYS)
                        .toInstant();
                yield new Instant[]{ monthStart, null };
            }
            case LAST_30_DAYS -> {
                Instant start = now.minus(30, ChronoUnit.DAYS).toInstant();
                yield new Instant[]{ start, null };
            }
            case ALL_TIME -> new Instant[]{ null, null };
        };
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
