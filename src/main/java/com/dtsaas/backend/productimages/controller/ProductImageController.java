package com.dtsaas.backend.productimages.controller;

import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.productimages.dto.ProductImageResponse;
import com.dtsaas.backend.productimages.dto.UpdateProductImageRequest;
import com.dtsaas.backend.productimages.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/businesses/{businessId}/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping
    public List<ProductImageResponse> list(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId) {
        return productImageService.list(businessId, productId, owner.id());
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse upload(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String alt,
            @RequestParam(required = false) Integer position,
            @RequestParam(required = false) Boolean isPrimary) {
        return productImageService.upload(businessId, productId, owner.id(), file, alt, position, isPrimary);
    }

    @PatchMapping("/{imageId}")
    public ProductImageResponse update(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId,
            @PathVariable UUID imageId,
            @Valid @RequestBody UpdateProductImageRequest request) {
        return productImageService.update(businessId, productId, imageId, owner.id(), request);
    }

    @PatchMapping("/{imageId}/set-primary")
    public ProductImageResponse setPrimary(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {
        return productImageService.setPrimary(businessId, productId, imageId, owner.id());
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {
        productImageService.delete(businessId, productId, imageId, owner.id());
    }
}
