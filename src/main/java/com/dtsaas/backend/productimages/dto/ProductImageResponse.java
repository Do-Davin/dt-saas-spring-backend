package com.dtsaas.backend.productimages.dto;

import com.dtsaas.backend.productimages.entity.ProductImage;

import java.time.Instant;
import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        UUID productId,
        String objectKey,
        String url,
        String alt,
        int position,
        boolean isPrimary,
        Instant createdAt,
        Instant updatedAt) {

    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getProduct().getId(),
                image.getObjectKey(),
                image.getUrl(),
                image.getAlt(),
                image.getPosition(),
                image.isPrimary(),
                image.getCreatedAt(),
                image.getUpdatedAt());
    }
}
