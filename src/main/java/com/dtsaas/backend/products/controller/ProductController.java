package com.dtsaas.backend.products.controller;

import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.products.dto.CreateProductRequest;
import com.dtsaas.backend.products.dto.ProductResponse;
import com.dtsaas.backend.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
