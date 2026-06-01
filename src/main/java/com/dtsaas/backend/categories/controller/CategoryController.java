package com.dtsaas.backend.categories.controller;

import com.dtsaas.backend.categories.dto.CategoryResponse;
import com.dtsaas.backend.categories.dto.CreateCategoryRequest;
import com.dtsaas.backend.categories.dto.UpdateCategoryRequest;
import com.dtsaas.backend.categories.service.CategoryService;
import com.dtsaas.backend.common.security.AuthenticatedOwner;
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
@RequestMapping("/api/businesses/{businessId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.create(businessId, owner.id(), request);
    }

    @GetMapping
    public List<CategoryResponse> list(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(value = "branchId", required = false) UUID branchId) {
        return categoryService.list(businessId, owner.id(), branchId);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse getOne(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId) {
        return categoryService.getOne(businessId, categoryId, owner.id());
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponse update(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.update(businessId, categoryId, owner.id(), request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId) {
        categoryService.delete(businessId, categoryId, owner.id());
    }
}
