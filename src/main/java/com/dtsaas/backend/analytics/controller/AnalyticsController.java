package com.dtsaas.backend.analytics.controller;

import com.dtsaas.backend.analytics.dto.AnalyticsOverviewResponse;
import com.dtsaas.backend.analytics.dto.AnalyticsPeriod;
import com.dtsaas.backend.analytics.service.AnalyticsService;
import com.dtsaas.backend.common.security.AuthenticatedOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/businesses/{businessId}/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public AnalyticsOverviewResponse overview(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(defaultValue = "THIS_MONTH") AnalyticsPeriod period) {
        return analyticsService.overview(businessId, owner.id(), period);
    }
}
