package com.dtsaas.backend.productimages.mapper;

import com.dtsaas.backend.common.storage.StorageService;
import com.dtsaas.backend.productimages.dto.ProductImageResponse;
import com.dtsaas.backend.productimages.entity.ProductImage;
import com.dtsaas.backend.products.dto.ProductPrimaryImageResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductImageMapper {

    private final StorageService storageService;

    public ProductImageResponse toOwnerResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getProduct().getId(),
                image.getObjectKey(),
                storageService.generateReadUrl(image.getObjectKey()),
                image.getAlt(),
                image.getPosition(),
                image.isPrimary(),
                image.getCreatedAt(),
                image.getUpdatedAt());
    }

    public ProductPrimaryImageResponse toPrimaryResponse(ProductImage image) {
        return new ProductPrimaryImageResponse(
                image.getId(),
                storageService.generateReadUrl(image.getObjectKey()),
                image.getAlt());
    }

    public PublicProductImageResponse toPublicResponse(ProductImage image) {
        return new PublicProductImageResponse(
                image.getId(),
                storageService.generateReadUrl(image.getObjectKey()),
                image.getAlt(),
                image.getPosition(),
                image.isPrimary());
    }
}
