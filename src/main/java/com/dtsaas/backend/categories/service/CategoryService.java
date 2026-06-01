package com.dtsaas.backend.categories.service;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.categories.dto.CategoryResponse;
import com.dtsaas.backend.categories.dto.CreateCategoryRequest;
import com.dtsaas.backend.categories.dto.UpdateCategoryRequest;
import com.dtsaas.backend.categories.entity.Category;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final BusinessService businessService;

    @Transactional
    public CategoryResponse create(UUID businessId, UUID ownerId, CreateCategoryRequest request) {
        Business business = businessService.requireOwnedBusiness(businessId, ownerId);
        Branch branch = null;
        if (request.branchId() != null) {
            branch = requireBranchInBusiness(request.branchId(), businessId);
        }
        Category category = new Category(
                business, branch,
                request.name(), request.nameKm(),
                request.position(), request.isActive());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID businessId, UUID ownerId, UUID branchIdFilter) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        if (branchIdFilter != null) {
            requireBranchInBusiness(branchIdFilter, businessId);
            return categoryRepository
                    .findAllByBusinessIdAndBranchIdOrderByPositionAscCreatedAtDesc(businessId, branchIdFilter)
                    .stream().map(CategoryResponse::from).toList();
        }
        return categoryRepository.findAllByBusinessIdOrderByPositionAscCreatedAtDesc(businessId)
                .stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getOne(UUID businessId, UUID categoryId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        return CategoryResponse.from(requireOwnedCategory(businessId, categoryId));
    }

    @Transactional
    public CategoryResponse update(UUID businessId, UUID categoryId, UUID ownerId, UpdateCategoryRequest request) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Category category = requireOwnedCategory(businessId, categoryId);

        if (request.branchId() != null) {
            Branch branch = requireBranchInBusiness(request.branchId(), businessId);
            category.setBranch(branch);
        }
        if (request.name() != null) {
            category.setName(request.name());
        }
        if (request.nameKm() != null) {
            category.setNameKm(request.nameKm());
        }
        if (request.position() != null) {
            category.setPosition(request.position());
        }
        if (request.isActive() != null) {
            category.setActive(request.isActive());
        }
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(UUID businessId, UUID categoryId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Category category = requireOwnedCategory(businessId, categoryId);
        // TODO: when Product entity exists, block delete if any non-deleted products reference this category
        categoryRepository.delete(category);
    }

    private Category requireOwnedCategory(UUID businessId, UUID categoryId) {
        return categoryRepository.findByIdAndBusinessId(categoryId, businessId)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
    }

    private Branch requireBranchInBusiness(UUID branchId, UUID businessId) {
        return branchRepository.findByIdAndBusinessId(branchId, businessId)
                .orElseThrow(() -> ApiException.notFound("Branch not found in this business"));
    }
}
