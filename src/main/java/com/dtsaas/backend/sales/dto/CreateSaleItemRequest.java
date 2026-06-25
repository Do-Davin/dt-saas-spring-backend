package com.dtsaas.backend.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSaleItemRequest(
        @NotNull UUID productId,
        @NotNull @Min(1) Integer quantity,
        @DecimalMin("0.00") BigDecimal unitSalesPrice,
        @DecimalMin("0.00") BigDecimal unitCostPrice,
        @DecimalMin("0.00") BigDecimal discountAmount) {
}
