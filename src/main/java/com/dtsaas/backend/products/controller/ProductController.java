package com.dtsaas.backend.products.controller;

import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.products.dto.CreateProductRequest;
import com.dtsaas.backend.products.dto.ProductResponse;
import com.dtsaas.backend.products.dto.UpdateProductRequest;
import com.dtsaas.backend.products.service.ProductService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/businesses/{businessId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @Valid @RequestBody CreateProductRequest request) {
        return productService.create(businessId, owner.id(), request);
    }

    @GetMapping
    public List<ProductResponse> list(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) Boolean isVisible) {
        return productService.list(businessId, owner.id(), branchId, categoryId, isAvailable, isVisible);
    }

    @GetMapping("/{productId}")
    public ProductResponse getOne(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId) {
        return productService.getOne(businessId, productId, owner.id());
    }

    @PatchMapping("/{productId}")
    public ProductResponse update(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(businessId, productId, owner.id(), request);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID productId) {
        productService.delete(businessId, productId, owner.id());
    }
}
