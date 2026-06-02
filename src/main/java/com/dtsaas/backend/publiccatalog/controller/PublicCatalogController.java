package com.dtsaas.backend.publiccatalog.controller;

import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.publiccatalog.dto.PublicBusinessResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicCatalogResponse;
import com.dtsaas.backend.publiccatalog.service.PublicCatalogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
