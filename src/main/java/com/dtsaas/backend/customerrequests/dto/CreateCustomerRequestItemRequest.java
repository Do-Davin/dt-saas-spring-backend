package com.dtsaas.backend.customerrequests.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCustomerRequestItemRequest(
        UUID productId,
        // Required when productId is absent — enforced in service
        @Size(max = 150) String productName,
        @Min(1) @Max(100) Integer quantity,
        @Size(max = 200) String note) {
}
