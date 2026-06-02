package com.dtsaas.backend.products.service;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.categories.entity.Category;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.products.dto.CreateProductRequest;
import com.dtsaas.backend.products.dto.ProductResponse;
import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.entity.UnitOfMeasure;
import com.dtsaas.backend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessService businessService;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse create(UUID businessId, UUID ownerId, CreateProductRequest request) {
        Business business = businessService.requireOwnedBusiness(businessId, ownerId);

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findByIdAndBusinessId(request.branchId(), businessId)
                    .orElseThrow(() -> ApiException.notFound("Branch not found in this business"));
        }

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndBusinessId(request.categoryId(), businessId)
                    .orElseThrow(() -> ApiException.notFound("Category not found in this business"));
        }

        if (branch != null && category != null) {
            Branch categoryBranch = category.getBranch();
            if (categoryBranch != null && !categoryBranch.getId().equals(branch.getId())) {
                throw ApiException.badRequest(
                        "Category belongs to a different branch. Use a business-level category or match the branch.");
            }
        }

        PricingType pricingType = request.pricingType() != null ? request.pricingType() : PricingType.FIXED;
        UnitOfMeasure uom = request.uom() != null ? request.uom() : UnitOfMeasure.UNIT;
        boolean isAvailable = request.isAvailable() == null || request.isAvailable();
        boolean isVisible = request.isVisible() == null || request.isVisible();

        Product product = new Product(
                business, branch, category,
                request.name(), request.nameKm(),
                request.description(), request.descriptionKm(),
                request.purchasePrice(), request.salesPrice(), request.discount(),
                pricingType, request.label(), uom,
                isAvailable, isVisible);

        product.setToppings(request.toppings());
        product.setIngredients(request.ingredients());

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(UUID businessId, UUID ownerId,
            UUID branchId, UUID categoryId,
            Boolean isAvailable, Boolean isVisible) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        if (branchId != null) {
            branchRepository.findByIdAndBusinessId(branchId, businessId)
                    .orElseThrow(() -> ApiException.notFound("Branch not found in this business"));
        }
        if (categoryId != null) {
            categoryRepository.findByIdAndBusinessId(categoryId, businessId)
                    .orElseThrow(() -> ApiException.notFound("Category not found in this business"));
        }

        Specification<Product> spec = Specification
                .where(hasBusinessId(businessId))
                .and(notDeleted())
                .and(hasBranchId(branchId))
                .and(hasCategoryId(categoryId))
                .and(hasIsAvailable(isAvailable))
                .and(hasIsVisible(isVisible));

        return productRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    // ─── Specifications ───────────────────────────────────────────────────────

    private static Specification<Product> hasBusinessId(UUID businessId) {
        return (root, query, cb) -> cb.equal(root.get("business").get("id"), businessId);
    }

    private static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
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

    private static Specification<Product> hasIsAvailable(Boolean isAvailable) {
        if (isAvailable == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), isAvailable);
    }

    private static Specification<Product> hasIsVisible(Boolean isVisible) {
        if (isVisible == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("isVisible"), isVisible);
    }
}
