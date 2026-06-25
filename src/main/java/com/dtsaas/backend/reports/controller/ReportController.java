package com.dtsaas.backend.reports.controller;

import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.reports.dto.CategoryRevenueResponse;
import com.dtsaas.backend.reports.dto.ProductRevenueResponse;
import com.dtsaas.backend.reports.dto.SalesSummaryResponse;
import com.dtsaas.backend.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/businesses/{businessId}/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-summary")
    public SalesSummaryResponse salesSummary(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) UUID branchId) {
        return reportService.salesSummary(businessId, owner.id(), from, to, branchId);
    }

    @GetMapping("/sales-by-category")
    public List<CategoryRevenueResponse> salesByCategory(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) UUID branchId) {
        return reportService.salesByCategory(businessId, owner.id(), from, to, branchId);
    }

    @GetMapping("/sales-by-product")
    public List<ProductRevenueResponse> salesByProduct(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) UUID branchId) {
        return reportService.salesByProduct(businessId, owner.id(), from, to, branchId);
    }
}
