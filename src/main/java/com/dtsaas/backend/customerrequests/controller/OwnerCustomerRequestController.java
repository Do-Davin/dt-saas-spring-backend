package com.dtsaas.backend.customerrequests.controller;

import com.dtsaas.backend.common.security.AuthenticatedOwner;
import com.dtsaas.backend.customerrequests.dto.OwnerRequestDetailResponse;
import com.dtsaas.backend.customerrequests.dto.OwnerRequestPageResponse;
import com.dtsaas.backend.customerrequests.dto.UpdateCustomerRequestStatusRequest;
import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import com.dtsaas.backend.customerrequests.entity.RequestType;
import com.dtsaas.backend.customerrequests.service.CustomerRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/businesses/{businessId}/requests")
@RequiredArgsConstructor
@Validated
public class OwnerCustomerRequestController {

    private final CustomerRequestService customerRequestService;

    @GetMapping
    public OwnerRequestPageResponse findAll(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestType type,
            @RequestParam(required = false) UUID branchId) {
        return customerRequestService.findAllForOwner(businessId, owner.id(), page, limit, status, type, branchId);
    }

    @GetMapping("/{requestId}")
    public OwnerRequestDetailResponse findOne(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID requestId) {
        return customerRequestService.findOneForOwner(businessId, requestId, owner.id());
    }

    @PatchMapping("/{requestId}/status")
    public OwnerRequestDetailResponse updateStatus(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @PathVariable UUID businessId,
            @PathVariable UUID requestId,
            @RequestBody @Valid UpdateCustomerRequestStatusRequest dto) {
        return customerRequestService.updateStatusForOwner(businessId, requestId, owner.id(), dto);
    }
}
