package com.dtsaas.backend.publiccatalog.service;

import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
import com.dtsaas.backend.categories.entity.Category;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.productimages.mapper.ProductImageMapper;
import com.dtsaas.backend.productimages.repository.ProductImageRepository;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import com.dtsaas.backend.publiccatalog.dto.PublicBranchResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicBusinessResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicCategoryResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductImageResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductPageResponse;
import com.dtsaas.backend.publiccatalog.dto.PublicProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCatalogQueryService {

        private final BusinessRepository businessRepository;
        private final BranchRepository branchRepository;
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final ProductImageRepository productImageRepository;
        private final ProductImageMapper imageMapper;

        public Optional<PublicBusinessResponse> findPublicBusiness(String slug) {
                return businessRepository.findBySlug(slug)
                                .map(PublicBusinessResponse::from);
        }

        public List<PublicBranchResponse> findPublicBranches(UUID businessId) {
                return branchRepository.findAllByBusinessIdOrderByNameAsc(businessId)
                                .stream()
                                .map(PublicBranchResponse::from)
                                .toList();
        }

        public List<PublicCategoryResponse> findPublicCategories(UUID businessId, UUID branchId) {
                List<Category> rows = branchId != null
                                ? categoryRepository
                                                .findAllByBusinessIdAndBranchIdAndIsActiveTrueOrderByPositionAscNameAsc(
                                                                businessId, branchId)
                                : categoryRepository.findAllByBusinessIdAndIsActiveTrueOrderByPositionAscNameAsc(
                                                businessId);
                return rows.stream().map(PublicCategoryResponse::from).toList();
        }

        public PublicProductPageResponse findPublicProducts(
                        UUID businessId,
                        UUID branchId,
                        String branchSlug,
                        UUID categoryId,
                        String search,
                        int page,
                        int limit) {

                UUID resolvedBranchId = branchId;
                if (resolvedBranchId == null && branchSlug != null) {
                        resolvedBranchId = branchRepository.findByBusinessIdAndSlug(businessId, branchSlug)
                                        .orElseThrow(() -> ApiException.notFound("Branch not found"))
                                        .getId();
                }

                Specification<Product> spec = Specification
                                .where(hasBusinessId(businessId))
                                .and(notDeleted())
                                .and(isVisible())
                                .and(hasBranchId(resolvedBranchId))
                                .and(hasCategoryId(categoryId))
                                .and(matchesSearch(search));

                Page<Product> productPage = productRepository.findAll(
                                spec,
                                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt")));

                List<UUID> productIds = productPage.stream().map(Product::getId).toList();

                Map<UUID, PublicProductImageResponse> primaryImageMap = productIds.isEmpty()
                                ? Map.of()
                                : productImageRepository.findAllByProductIdInAndIsPrimaryTrue(productIds)
                                                .stream()
                                                .collect(Collectors.toMap(
                                                                img -> img.getProduct().getId(),
                                                                imageMapper::toPublicResponse));

                List<PublicProductResponse> items = productPage.stream()
                                .map(p -> PublicProductResponse.from(p, primaryImageMap.get(p.getId()), null))
                                .toList();

                return new PublicProductPageResponse(
                                items,
                                new PublicProductPageResponse.Pagination(
                                                page, limit, productPage.getTotalElements(),
                                                productPage.getTotalPages()));
        }

        public Optional<PublicProductResponse> findPublicProduct(UUID businessId, UUID productId) {
                return productRepository
                                .findByIdAndBusinessIdAndDeletedAtIsNullAndIsVisibleTrue(productId, businessId)
                                .map(product -> {
                                        List<PublicProductImageResponse> images = productImageRepository
                                                        .findAllByProductIdOrderByPositionAscCreatedAtAsc(
                                                                        product.getId())
                                                        .stream()
                                                        .map(imageMapper::toPublicResponse)
                                                        .toList();
                                        PublicProductImageResponse primary = images.stream()
                                                        .filter(PublicProductImageResponse::isPrimary)
                                                        .findFirst()
                                                        .orElse(null);
                                        return PublicProductResponse.from(product, primary, images);
                                });
        }

        // ─── Specification helpers ────────────────────────────────────────────────

        private static Specification<Product> hasBusinessId(UUID businessId) {
                return (root, query, cb) -> cb.equal(root.get("business").get("id"), businessId);
        }

        private static Specification<Product> notDeleted() {
                return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        }

        private static Specification<Product> isVisible() {
                return (root, query, cb) -> cb.isTrue(root.get("isVisible"));
        }

        private static Specification<Product> hasBranchId(UUID branchId) {
                if (branchId == null)
                        return null;
                return (root, query, cb) -> cb.equal(root.get("branch").get("id"), branchId);
        }

        private static Specification<Product> hasCategoryId(UUID categoryId) {
                if (categoryId == null)
                        return null;
                return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
        }

        private static Specification<Product> matchesSearch(String search) {
                if (search == null || search.isBlank())
                        return null;
                String pattern = "%" + search.trim().toLowerCase() + "%";
                return (root, query, cb) -> cb.or(
                                cb.like(cb.lower(root.get("name")), pattern),
                                cb.like(cb.lower(root.get("nameKm")), pattern),
                                cb.like(cb.lower(root.get("description")), pattern),
                                cb.like(cb.lower(root.get("descriptionKm")), pattern));
        }
}
