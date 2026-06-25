package com.dtsaas.backend.branches.controller;

import com.dtsaas.backend.branches.dto.BranchResponse;
import com.dtsaas.backend.branches.dto.CreateBranchRequest;
import com.dtsaas.backend.branches.dto.UpdateBranchRequest;
import com.dtsaas.backend.branches.service.BranchService;
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
@RequestMapping("/api/businesses/{businessId}/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponse create(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @Valid @RequestBody CreateBranchRequest request) {
        RoleGuard.requireSuperAdmin(owner);
        return branchService.create(businessId, owner.id(), request);
    }

    @GetMapping
    public List<BranchResponse> list(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId) {
        RoleGuard.requireSuperAdmin(owner);
        return branchService.list(businessId, owner.id());
    }

    @GetMapping("/{branchId}")
    public BranchResponse getOne(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID branchId) {
        RoleGuard.requireSuperAdmin(owner);
        return branchService.getOne(businessId, branchId, owner.id());
    }

    @PatchMapping("/{branchId}")
    public BranchResponse update(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateBranchRequest request) {
        RoleGuard.requireSuperAdmin(owner);
        return branchService.update(businessId, branchId, owner.id(), request);
    }

    @DeleteMapping("/{branchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID branchId) {
        RoleGuard.requireSuperAdmin(owner);
        branchService.delete(businessId, branchId, owner.id());
    }
}
