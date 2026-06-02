package com.dtsaas.backend.productimages.service;

import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.common.storage.StorageService;
import com.dtsaas.backend.productimages.dto.ProductImageResponse;
import com.dtsaas.backend.productimages.entity.ProductImage;
import com.dtsaas.backend.productimages.repository.ProductImageRepository;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> MIME_TO_EXT = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final BusinessService businessService;
    private final StorageService storageService;

    @Transactional
    public ProductImageResponse upload(UUID businessId, UUID productId, UUID ownerId,
                                       MultipartFile file, String alt, Integer position, Boolean isPrimary) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Product product = requireProduct(businessId, productId);

        String contentType = file.getContentType();
        if (contentType == null || !MIME_TO_EXT.containsKey(contentType)) {
            throw ApiException.badRequest("Unsupported file type. Allowed: image/jpeg, image/png, image/webp");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw ApiException.badRequest("File exceeds the 5 MB limit");
        }

        UUID imageId = UUID.randomUUID();
        String extension = MIME_TO_EXT.get(contentType);
        String key = storageService.buildProductImageKey(businessId, productId, imageId, extension);

        try {
            storageService.uploadObject(key, file.getInputStream(), file.getSize(), contentType);
        } catch (IOException e) {
            throw ApiException.badRequest("Failed to read upload file");
        }

        try {
            long existingCount = productImageRepository.countByProductId(productId);
            boolean makePrimary = existingCount == 0 || Boolean.TRUE.equals(isPrimary);

            if (makePrimary) {
                productImageRepository.demotePrimaries(productId);
            }

            ProductImage image = new ProductImage(
                    product, key, null, alt,
                    position != null ? position : 0,
                    makePrimary);

            return ProductImageResponse.from(productImageRepository.save(image));
        } catch (RuntimeException e) {
            try { storageService.deleteObject(key); } catch (Exception ignored) {}
            throw e;
        }
    }

    private Product requireProduct(UUID businessId, UUID productId) {
        return productRepository.findByIdAndBusinessIdAndDeletedAtIsNull(productId, businessId)
                .orElseThrow(() -> ApiException.notFound("Product not found"));
    }
}
