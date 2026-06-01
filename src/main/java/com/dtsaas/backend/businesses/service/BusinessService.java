package com.dtsaas.backend.businesses.service;

import com.dtsaas.backend.auth.entity.Owner;
import com.dtsaas.backend.auth.repository.OwnerRepository;
import com.dtsaas.backend.businesses.dto.BusinessResponse;
import com.dtsaas.backend.businesses.dto.CreateBusinessRequest;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
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
}
