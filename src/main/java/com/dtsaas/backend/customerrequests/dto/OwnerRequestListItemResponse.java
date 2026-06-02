package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import com.dtsaas.backend.customerrequests.entity.RequestType;

import java.time.Instant;
import java.util.UUID;

public record OwnerRequestListItemResponse(
        UUID id,
        UUID branchId,
        RequestType type,
        RequestStatus status,
        String customerName,
        String customerPhone,
        Instant createdAt) {

    public static OwnerRequestListItemResponse from(CustomerRequest request) {
        return new OwnerRequestListItemResponse(
                request.getId(),
                request.getBranch() != null ? request.getBranch().getId() : null,
                request.getType(),
                request.getStatus(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getCreatedAt());
    }
}
