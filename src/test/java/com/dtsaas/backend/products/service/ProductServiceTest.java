package com.dtsaas.backend.products.service;

import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.productimages.entity.ProductImage;
import com.dtsaas.backend.productimages.mapper.ProductImageMapper;
import com.dtsaas.backend.productimages.repository.ProductImageRepository;
import com.dtsaas.backend.products.dto.ProductPrimaryImageResponse;
import com.dtsaas.backend.products.dto.ProductResponse;
import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.entity.UnitOfMeasure;
import com.dtsaas.backend.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

        @Mock
        ProductRepository productRepository;
        @Mock
        BusinessService businessService;
        @Mock
        BranchRepository branchRepository;
        @Mock
        CategoryRepository categoryRepository;
        @Mock
        ProductImageRepository productImageRepository;
        @Mock
        ProductImageMapper productImageMapper;

        @InjectMocks
        ProductService productService;

        // ─── list() ──────────────────────────────────────────────────────────────

        @Test
        void list_includesPrimaryImage_whenProductHasPrimaryImage() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();

                Product product = stubProduct(productId, businessId);
                when(productRepository.findAll(any(Specification.class), any(Sort.class)))
                                .thenReturn(List.of(product));

                // list() calls img.getProduct().getId() to build the productId→DTO map.
                ProductImage image = stubImageWithProductId(productId);
                when(productImageRepository.findAllByProductIdInAndIsPrimaryTrue(List.of(productId)))
                                .thenReturn(List.of(image));

                ProductPrimaryImageResponse dto = new ProductPrimaryImageResponse(UUID.randomUUID(),
                                "https://cdn.example.com/img.jpg", "alt");
                when(productImageMapper.toPrimaryResponse(image)).thenReturn(dto);

                List<ProductResponse> result = productService.list(businessId, ownerId, null, null, null, null);

                assertEquals(1, result.size());
                assertNotNull(result.get(0).primaryImage());
                assertEquals(dto.url(), result.get(0).primaryImage().url());
                assertEquals(dto.id(), result.get(0).primaryImage().id());
        }

        @Test
        void list_returnsNullPrimaryImage_whenProductHasNoImages() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();

                Product product = stubProduct(productId, businessId);
                when(productRepository.findAll(any(Specification.class), any(Sort.class)))
                                .thenReturn(List.of(product));
                when(productImageRepository.findAllByProductIdInAndIsPrimaryTrue(List.of(productId)))
                                .thenReturn(List.of());

                List<ProductResponse> result = productService.list(businessId, ownerId, null, null, null, null);

                assertEquals(1, result.size());
                assertNull(result.get(0).primaryImage());
        }

        @Test
        void list_returnsOnlyPrimaryImage_notAllImages() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();
                UUID imageId = UUID.randomUUID();

                Product product = stubProduct(productId, businessId);
                when(productRepository.findAll(any(Specification.class), any(Sort.class)))
                                .thenReturn(List.of(product));

                // Repository returns only the row where isPrimary=true — non-primary images
                // never arrive.
                ProductImage image = stubImageWithProductId(productId);
                when(productImageRepository.findAllByProductIdInAndIsPrimaryTrue(List.of(productId)))
                                .thenReturn(List.of(image));

                ProductPrimaryImageResponse dto = new ProductPrimaryImageResponse(imageId,
                                "https://cdn.example.com/primary.jpg", null);
                when(productImageMapper.toPrimaryResponse(image)).thenReturn(dto);

                List<ProductResponse> result = productService.list(businessId, ownerId, null, null, null, null);

                assertNotNull(result.get(0).primaryImage());
                assertEquals(dto.url(), result.get(0).primaryImage().url());
                assertEquals(imageId, result.get(0).primaryImage().id());
        }

        @Test
        void list_skipsImageQuery_whenProductListIsEmpty() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();

                when(productRepository.findAll(any(Specification.class), any(Sort.class)))
                                .thenReturn(List.of());

                List<ProductResponse> result = productService.list(businessId, ownerId, null, null, null, null);

                assertEquals(0, result.size());
                verify(productImageRepository, never()).findAllByProductIdInAndIsPrimaryTrue(anyCollection());
        }

        @Test
        void list_throws_whenBusinessNotOwnedByRequester() {
                UUID businessId = UUID.randomUUID();
                UUID wrongOwnerId = UUID.randomUUID();

                when(businessService.requireOwnedBusiness(businessId, wrongOwnerId))
                                .thenThrow(ApiException.notFound("Business not found"));

                assertThrows(ApiException.class,
                                () -> productService.list(businessId, wrongOwnerId, null, null, null, null));

                verify(productRepository, never()).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        void list_returnsEmpty_whenRepositoryReturnsNoNonDeletedProducts() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();

                when(productRepository.findAll(any(Specification.class), any(Sort.class)))
                                .thenReturn(List.of());

                List<ProductResponse> result = productService.list(businessId, ownerId, null, null, null, null);

                assertEquals(0, result.size());
        }

        // ─── getOne() ────────────────────────────────────────────────────────────

        @Test
        void getOne_includesPrimaryImage_whenProductHasPrimaryImage() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();

                Product product = stubProduct(productId, businessId);
                when(productRepository.findByIdAndBusinessIdAndDeletedAtIsNull(productId, businessId))
                                .thenReturn(Optional.of(product));

                // getOne() does NOT call img.getProduct().getId() — it just maps the first
                // image via the mapper.
                ProductImage image = mock(ProductImage.class);
                when(productImageRepository.findAllByProductIdInAndIsPrimaryTrue(List.of(productId)))
                                .thenReturn(List.of(image));

                ProductPrimaryImageResponse dto = new ProductPrimaryImageResponse(UUID.randomUUID(),
                                "https://cdn.example.com/img.jpg", "alt");
                when(productImageMapper.toPrimaryResponse(image)).thenReturn(dto);

                ProductResponse result = productService.getOne(businessId, productId, ownerId);

                assertNotNull(result.primaryImage());
                assertEquals(dto.url(), result.primaryImage().url());
        }

        @Test
        void getOne_returnsNullPrimaryImage_whenProductHasNoImages() {
                UUID businessId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();

                Product product = stubProduct(productId, businessId);
                when(productRepository.findByIdAndBusinessIdAndDeletedAtIsNull(productId, businessId))
                                .thenReturn(Optional.of(product));
                when(productImageRepository.findAllByProductIdInAndIsPrimaryTrue(List.of(productId)))
                                .thenReturn(List.of());

                ProductResponse result = productService.getOne(businessId, productId, ownerId);

                assertNull(result.primaryImage());
        }

        // ─── contract: no storage key in primaryImage ─────────────────────────────

        @Test
        void primaryImageResponse_doesNotExposeObjectKey() {
                // Compile-time: ProductPrimaryImageResponse has no objectKey field.
                // Verify at runtime that the record accessor doesn't exist.
                assertThrows(NoSuchMethodException.class,
                                () -> ProductPrimaryImageResponse.class.getMethod("objectKey"));

                assertDoesNotThrow(() -> ProductPrimaryImageResponse.class.getMethod("id"));
                assertDoesNotThrow(() -> ProductPrimaryImageResponse.class.getMethod("url"));
                assertDoesNotThrow(() -> ProductPrimaryImageResponse.class.getMethod("alt"));
        }

        // ─── Helpers ─────────────────────────────────────────────────────────────

        private Product stubProduct(UUID productId, UUID businessId) {
                Business business = mock(Business.class);
                when(business.getId()).thenReturn(businessId);

                Product product = mock(Product.class);
                when(product.getId()).thenReturn(productId);
                when(product.getBusiness()).thenReturn(business);
                when(product.getName()).thenReturn("Test Product");
                when(product.getPricingType()).thenReturn(PricingType.FIXED);
                when(product.getUom()).thenReturn(UnitOfMeasure.UNIT);
                when(product.isAvailable()).thenReturn(true);
                when(product.isVisible()).thenReturn(true);
                when(product.getSalesPrice()).thenReturn(BigDecimal.TEN);
                return product;
        }

        // Stubs only what list() accesses: img.getProduct().getId() for map-key
        // construction.
        private ProductImage stubImageWithProductId(UUID productId) {
                Product productProxy = mock(Product.class);
                when(productProxy.getId()).thenReturn(productId);

                ProductImage image = mock(ProductImage.class);
                when(image.getProduct()).thenReturn(productProxy);
                return image;
        }
}
