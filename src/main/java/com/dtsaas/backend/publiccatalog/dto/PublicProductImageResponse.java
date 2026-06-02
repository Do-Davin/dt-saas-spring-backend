package com.dtsaas.backend.publiccatalog.dto;

import com.dtsaas.backend.productimages.entity.ProductImage;

import java.util.UUID;

public record PublicProductImageResponse(
        UUID id,
        String url,
        String alt,
        int position,
        boolean isPrimary) {

    public static PublicProductImageResponse from(ProductImage image) {
        return new PublicProductImageResponse(
                image.getId(),
                image.getUrl(),
                image.getAlt(),
                image.getPosition(),
                image.isPrimary());
    }
}
