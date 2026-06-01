package com.dtsaas.backend.branches.service;

import com.dtsaas.backend.branches.dto.BranchResponse;
import com.dtsaas.backend.branches.dto.CreateBranchRequest;
import com.dtsaas.backend.branches.dto.UpdateBranchRequest;
import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessService businessService;

    @Transactional
    public BranchResponse create(UUID businessId, UUID ownerId, CreateBranchRequest request) {
        Business business = businessService.requireOwnedBusiness(businessId, ownerId);
        if (branchRepository.existsByBusinessIdAndSlug(businessId, request.slug())) {
            throw ApiException.conflict("Slug is already taken in this business");
        }
        Branch branch = new Branch(business, request.name(), request.nameKm(), request.slug());
        return BranchResponse.from(branchRepository.save(branch));
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> list(UUID businessId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        return branchRepository.findAllByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream()
                .map(BranchResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse getOne(UUID businessId, UUID branchId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        return BranchResponse.from(requireOwnedBranch(businessId, branchId));
    }

    @Transactional
    public BranchResponse update(UUID businessId, UUID branchId, UUID ownerId, UpdateBranchRequest request) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Branch branch = requireOwnedBranch(businessId, branchId);

        if (request.slug() != null && !request.slug().equals(branch.getSlug())) {
            if (branchRepository.existsByBusinessIdAndSlugAndIdNot(businessId, request.slug(), branchId)) {
                throw ApiException.conflict("Slug is already taken in this business");
            }
            branch.setSlug(request.slug());
        }
        if (request.name() != null) {
            branch.setName(request.name());
        }
        if (request.nameKm() != null) {
            branch.setNameKm(request.nameKm());
        }
        return BranchResponse.from(branch);
    }

    @Transactional
    public void delete(UUID businessId, UUID branchId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Branch branch = requireOwnedBranch(businessId, branchId);
        if (categoryRepository.existsByBranchId(branchId)) {
            throw ApiException.conflict(
                    "Cannot delete branch with existing categories. Remove them first.");
        }
        branchRepository.delete(branch);
    }

    private Branch requireOwnedBranch(UUID businessId, UUID branchId) {
        return branchRepository.findByIdAndBusinessId(branchId, businessId)
                .orElseThrow(() -> ApiException.notFound("Branch not found"));
    }
}
