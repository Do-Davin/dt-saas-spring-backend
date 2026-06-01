package com.dtsaas.backend.businesses.service;

import com.dtsaas.backend.auth.entity.Owner;
import com.dtsaas.backend.auth.repository.OwnerRepository;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.dto.BusinessResponse;
import com.dtsaas.backend.businesses.dto.CreateBusinessRequest;
import com.dtsaas.backend.businesses.dto.UpdateBusinessRequest;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
import com.dtsaas.backend.categories.repository.CategoryRepository;
import com.dtsaas.backend.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final OwnerRepository ownerRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BusinessResponse create(UUID ownerId, CreateBusinessRequest request) {
        if (businessRepository.existsBySlug(request.slug())) {
            throw ApiException.conflict("Slug is already taken");
        }

        Owner ownerRef = ownerRepository.getReferenceById(ownerId);
        Business business = new Business(
                ownerRef,
                request.name(),
                request.nameKm(),
                request.slug(),
                request.type(),
                request.catalogMode());

        return BusinessResponse.from(businessRepository.save(business));
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> listForOwner(UUID ownerId) {
        return businessRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(BusinessResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponse getOne(UUID businessId, UUID ownerId) {
        return BusinessResponse.from(requireOwnedBusiness(businessId, ownerId));
    }

    @Transactional
    public BusinessResponse update(UUID businessId, UUID ownerId, UpdateBusinessRequest request) {
        Business business = requireOwnedBusiness(businessId, ownerId);

        if (request.slug() != null && !request.slug().equals(business.getSlug())) {
            if (businessRepository.existsBySlug(request.slug())) {
                throw ApiException.conflict("Slug is already taken");
            }
            business.setSlug(request.slug());
        }
        if (request.name() != null) {
            business.setName(request.name());
        }
        if (request.nameKm() != null) {
            business.setNameKm(request.nameKm());
        }
        if (request.type() != null) {
            business.setType(request.type());
        }
        if (request.catalogMode() != null) {
            business.setCatalogMode(request.catalogMode());
        }
        return BusinessResponse.from(business);
    }

    @Transactional
    public void delete(UUID businessId, UUID ownerId) {
        Business business = requireOwnedBusiness(businessId, ownerId);
        if (branchRepository.existsByBusinessId(businessId)
                || categoryRepository.existsByBusinessId(businessId)) {
            throw ApiException.conflict(
                    "Cannot delete business with existing branches or categories. Remove them first.");
        }
        businessRepository.delete(business);
    }

    @Transactional(readOnly = true)
    public Business requireOwnedBusiness(UUID businessId, UUID ownerId) {
        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> ApiException.notFound("Business not found"));
    }
}
