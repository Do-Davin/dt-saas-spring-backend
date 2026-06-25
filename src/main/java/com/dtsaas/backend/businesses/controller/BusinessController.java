package com.dtsaas.backend.businesses.controller;

import com.dtsaas.backend.businesses.dto.BusinessResponse;
import com.dtsaas.backend.businesses.dto.CreateBusinessRequest;
import com.dtsaas.backend.businesses.dto.UpdateBusinessRequest;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.common.security.RoleGuard;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessResponse create(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @Valid @RequestBody CreateBusinessRequest request) {
        RoleGuard.requireSuperAdmin(owner);
        return businessService.create(owner.id(), request);
    }

    @GetMapping
    public List<BusinessResponse> list(@AuthenticationPrincipal AuthenticatedOwner owner) {
        if ("SUPER_ADMIN".equals(owner.role())) {
            return businessService.listAll();
        }
        return businessService.listForOwner(owner.id());
    }

    @GetMapping("/{businessId}")
    public BusinessResponse getOne(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId) {
        if ("SUPER_ADMIN".equals(owner.role())) {
            return businessService.getAny(businessId);
        }
        return businessService.getOne(businessId, owner.id());
    }

    @PatchMapping("/{businessId}")
    public BusinessResponse update(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @Valid @RequestBody UpdateBusinessRequest request) {
        RoleGuard.requireSuperAdmin(owner);
        return businessService.update(businessId, owner.id(), request);
    }

    @DeleteMapping("/{businessId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId) {
        RoleGuard.requireSuperAdmin(owner);
        businessService.delete(businessId, owner.id());
    }
}
