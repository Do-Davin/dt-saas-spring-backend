package com.dtsaas.backend.productimages.dto;

import jakarta.validation.constraints.Min;

public record UpdateProductImageRequest(
        String alt,
        @Min(0) Integer position) {
}
