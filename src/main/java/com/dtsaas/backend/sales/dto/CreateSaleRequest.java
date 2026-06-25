package com.dtsaas.backend.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateSaleRequest(
        UUID branchId,
        Instant saleDate,
        String note,
        @NotNull @Size(min = 1, message = "At least one item is required") @Valid
        List<CreateSaleItemRequest> items) {
}
