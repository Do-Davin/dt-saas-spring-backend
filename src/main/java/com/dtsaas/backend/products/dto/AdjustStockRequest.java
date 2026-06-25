package com.dtsaas.backend.products.dto;

import com.dtsaas.backend.products.entity.StockAdjustmentReason;
import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(
        @NotNull Integer adjustment,
        @NotNull StockAdjustmentReason reason) {
}
