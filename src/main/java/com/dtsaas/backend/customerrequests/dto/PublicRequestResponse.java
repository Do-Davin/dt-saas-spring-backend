package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import com.dtsaas.backend.customerrequests.entity.RequestType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicRequestResponse(
        UUID id,
        RequestType type,
        RequestStatus status,
        UUID branchId,
        String customerName,
        String customerPhone,
        String customerNote,
        List<PublicRequestItemResponse> items,
        Instant createdAt) {

    public static PublicRequestResponse from(CustomerRequest request) {
        List<PublicRequestItemResponse> items = request.getItems().stream()
                .map(PublicRequestItemResponse::from)
                .toList();
        return new PublicRequestResponse(
                request.getId(),
                request.getType(),
                request.getStatus(),
                request.getBranch() != null ? request.getBranch().getId() : null,
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getCustomerNote(),
                items,
                request.getCreatedAt());
    }
}
