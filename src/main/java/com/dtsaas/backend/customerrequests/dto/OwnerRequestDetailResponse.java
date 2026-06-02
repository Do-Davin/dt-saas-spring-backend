package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import com.dtsaas.backend.customerrequests.entity.RequestType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OwnerRequestDetailResponse(
        UUID id,
        UUID businessId,
        UUID branchId,
        RequestType type,
        RequestStatus status,
        String customerName,
        String customerPhone,
        String customerNote,
        List<OwnerRequestItemResponse> items,
        Instant createdAt,
        Instant updatedAt) {

    public static OwnerRequestDetailResponse from(CustomerRequest request) {
        List<OwnerRequestItemResponse> items = request.getItems().stream()
                .map(OwnerRequestItemResponse::from)
                .toList();
        return new OwnerRequestDetailResponse(
                request.getId(),
                request.getBusiness().getId(),
                request.getBranch() != null ? request.getBranch().getId() : null,
                request.getType(),
                request.getStatus(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getCustomerNote(),
                items,
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}
