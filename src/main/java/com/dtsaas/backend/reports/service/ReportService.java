package com.dtsaas.backend.reports.service;

import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.reports.dto.CategoryRevenueResponse;
import com.dtsaas.backend.reports.dto.ProductRevenueResponse;
import com.dtsaas.backend.reports.dto.SalesSummaryResponse;
import com.dtsaas.backend.sales.repository.SaleItemRepository;
import com.dtsaas.backend.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BusinessService businessService;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    @Transactional(readOnly = true)
    public SalesSummaryResponse salesSummary(UUID businessId, UUID ownerId,
                                             Instant from, Instant to, UUID branchId) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        SaleRepository.SalesSummaryRow row =
                saleRepository.findSalesSummary(businessId, from, to, branchId);

        BigDecimal revenue = nvl(row.getTotalRevenue());
        BigDecimal cost    = nvl(row.getTotalCost());
        BigDecimal profit  = nvl(row.getTotalProfit());
        long count         = row.getSaleCount() != null ? row.getSaleCount() : 0L;

        return new SalesSummaryResponse(revenue, cost, profit, count);
    }

    @Transactional(readOnly = true)
    public List<CategoryRevenueResponse> salesByCategory(UUID businessId, UUID ownerId,
                                                         Instant from, Instant to, UUID branchId) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        return saleItemRepository
                .findRevenueByCategory(businessId, from, to, branchId)
                .stream()
                .map(r -> new CategoryRevenueResponse(
                        r.getCategoryId(),
                        r.getCategoryName(),
                        nvl(r.getTotalRevenue()),
                        nvl(r.getTotalCost()),
                        nvl(r.getTotalRevenue()).subtract(nvl(r.getTotalCost())),
                        r.getQuantity() != null ? r.getQuantity() : 0L))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductRevenueResponse> salesByProduct(UUID businessId, UUID ownerId,
                                                       Instant from, Instant to, UUID branchId) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        return saleItemRepository
                .findRevenueByProduct(businessId, from, to, branchId)
                .stream()
                .map(r -> new ProductRevenueResponse(
                        r.getProductId(),
                        r.getProductName(),
                        nvl(r.getTotalRevenue()),
                        nvl(r.getTotalCost()),
                        nvl(r.getTotalRevenue()).subtract(nvl(r.getTotalCost())),
                        r.getQuantity() != null ? r.getQuantity() : 0L))
                .toList();
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
