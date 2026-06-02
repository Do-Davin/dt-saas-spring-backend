package com.dtsaas.backend.customerrequests.dto;

import com.dtsaas.backend.customerrequests.entity.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateCustomerRequestRequest(
        @NotNull RequestType type,
        UUID branchId,
        @Size(max = 100) String customerName,
        @Size(max = 30) String customerPhone,
        @Size(max = 500) String customerNote,
        @Valid List<CreateCustomerRequestItemRequest> items) {
}
