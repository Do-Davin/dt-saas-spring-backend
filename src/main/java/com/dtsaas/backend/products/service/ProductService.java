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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
