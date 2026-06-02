package com.dtsaas.backend.publiccatalog.controller;

import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.publiccatalog.dto.PublicBusinessResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicCatalogResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductDetailResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductPageResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductResponse;
import com.dtsaas.backend.publiccatalog.service.PublicCatalogQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final PublicCatalogQueryService catalogQueryService;

    @GetMapping("/{businessSlug}")
    public PublicCatalogResponse getBusinessCatalog(@PathVariable String businessSlug) {
        PublicBusinessResponse business = catalogQueryService.findPublicBusiness(businessSlug)
                .orElseThrow(() -> ApiException.notFound("Business not found"));

        return new PublicCatalogResponse(
                business,
                catalogQueryService.findPublicBranches(business.id()),
                catalogQueryService.findPublicCategories(business.id(), null));
    }

    @GetMapping("/{businessSlug}/products")
    public PublicProductPageResponse getBusinessProducts(
            @PathVariable String businessSlug,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) String branchSlug,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @Size(max = 100) String search) {

        PublicBusinessResponse business = catalogQueryService.findPublicBusiness(businessSlug)
                .orElseThrow(() -> ApiException.notFound("Business not found"));

        return catalogQueryService.findPublicProducts(
                business.id(), branchId, branchSlug, categoryId, search, page, limit);
    }

    @GetMapping("/{businessSlug}/products/{productId}")
    public PublicProductDetailResponse getBusinessProduct(
            @PathVariable String businessSlug,
            @PathVariable UUID productId) {
        PublicBusinessResponse business = catalogQueryService.findPublicBusiness(businessSlug)
                .orElseThrow(() -> ApiException.notFound("Business not found"));
        PublicProductResponse product = catalogQueryService.findPublicProduct(business.id(), productId)
                .orElseThrow(() -> ApiException.notFound("Product not found"));
        return new PublicProductDetailResponse(product);
    }
}
