package com.dtsaas.backend.customerrequests.service;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.customerrequests.dto.CreateCustomerRequestItemRequest;
import com.dtsaas.backend.customerrequests.dto.CreateCustomerRequestRequest;
import com.dtsaas.backend.customerrequests.dto.OwnerRequestDetailResponse;
import com.dtsaas.backend.customerrequests.dto.OwnerRequestListItemResponse;
import com.dtsaas.backend.customerrequests.dto.OwnerRequestPageResponse;
import com.dtsaas.backend.customerrequests.dto.PublicRequestResponse;
import com.dtsaas.backend.customerrequests.dto.UpdateCustomerRequestStatusRequest;
import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import com.dtsaas.backend.customerrequests.entity.CustomerRequestItem;
import com.dtsaas.backend.customerrequests.entity.RequestStatus;
import com.dtsaas.backend.customerrequests.entity.RequestType;
import com.dtsaas.backend.customerrequests.repository.CustomerRequestRepository;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRequestService {

    private final BusinessService businessService;
    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final CustomerRequestRepository customerRequestRepository;

    @Transactional
    public PublicRequestResponse submitRequest(String businessSlug, CreateCustomerRequestRequest dto) {
        Business business = businessRepository.findBySlug(businessSlug)
                .orElseThrow(() -> ApiException.notFound("Business not found"));

        List<CreateCustomerRequestItemRequest> items = dto.items() != null ? dto.items() : List.of();

        assertTypeItemConstraints(dto.type(), items.size());

        Branch branch = null;
        if (dto.branchId() != null) {
            branch = branchRepository.findByIdAndBusinessId(dto.branchId(), business.getId())
                    .orElseThrow(() -> ApiException.notFound("Branch not found"));
        }

        CustomerRequest request = new CustomerRequest(
                business, branch, dto.type(),
                dto.customerName(), dto.customerPhone(), dto.customerNote());

        for (CreateCustomerRequestItemRequest itemDto : items) {
            request.addItem(buildItem(request, business.getId(), dto.type(), itemDto));
        }

        return PublicRequestResponse.from(customerRequestRepository.save(request));
    }

    // ─── Owner: list ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OwnerRequestPageResponse findAllForOwner(UUID businessId, UUID ownerId,
            int page, int limit,
            RequestStatus status, RequestType type,
            UUID branchId) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        if (branchId != null) {
            branchRepository.findByIdAndBusinessId(branchId, businessId)
                    .orElseThrow(() -> ApiException.notFound("Branch not found"));
        }

        Specification<CustomerRequest> spec = Specification
                .where(hasBusinessId(businessId));
        if (status != null) spec = spec.and(hasStatus(status));
        if (type != null) spec = spec.and(hasType(type));
        if (branchId != null) spec = spec.and(hasBranchId(branchId));

        Page<CustomerRequest> resultPage = customerRequestRepository.findAll(
                spec, PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<OwnerRequestListItemResponse> items = resultPage.stream()
                .map(OwnerRequestListItemResponse::from)
                .toList();

        return new OwnerRequestPageResponse(
                items,
                new OwnerRequestPageResponse.Pagination(
                        page, limit, resultPage.getTotalElements(), resultPage.getTotalPages()));
    }

    // ─── Owner: detail ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OwnerRequestDetailResponse findOneForOwner(UUID businessId, UUID requestId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        CustomerRequest request = customerRequestRepository.findByIdAndBusinessId(requestId, businessId)
                .orElseThrow(() -> ApiException.notFound("Request not found"));

        return OwnerRequestDetailResponse.from(request);
    }

    // ─── Owner: status update ─────────────────────────────────────────────────

    @Transactional
    public OwnerRequestDetailResponse updateStatusForOwner(UUID businessId, UUID requestId,
            UUID ownerId,
            UpdateCustomerRequestStatusRequest dto) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        CustomerRequest request = customerRequestRepository.findByIdAndBusinessId(requestId, businessId)
                .orElseThrow(() -> ApiException.notFound("Request not found"));

        if (request.getStatus() == dto.status()) {
            return OwnerRequestDetailResponse.from(request);
        }

        if (!request.getStatus().canTransitionTo(dto.status())) {
            throw ApiException.badRequest(
                    "Cannot transition from " + request.getStatus() + " to " + dto.status());
        }

        request.setStatus(dto.status());
        return OwnerRequestDetailResponse.from(customerRequestRepository.save(request));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void assertTypeItemConstraints(RequestType type, int count) {
        if (type == RequestType.ORDER && count < 1) {
            throw ApiException.badRequest("ORDER requests must include at least one item");
        }
        if (type == RequestType.BOOKING && count > 0) {
            throw ApiException.badRequest("BOOKING requests must not include items");
        }
    }

    private CustomerRequestItem buildItem(CustomerRequest request, UUID businessId,
            RequestType type, CreateCustomerRequestItemRequest dto) {
        if (dto.productId() != null) {
            Product product = productRepository
                    .findByIdAndBusinessIdAndDeletedAtIsNullAndIsVisibleTrue(dto.productId(), businessId)
                    .orElseThrow(() -> ApiException.notFound(
                            "Product not found or unavailable: " + dto.productId()));

            if (type == RequestType.ORDER && !product.isAvailable()) {
                throw new ApiException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "Product is not available for ordering: " + product.getName());
            }

            return new CustomerRequestItem(
                    request,
                    product,
                    product.getName(),
                    product.getSalesPrice(),
                    product.getPricingType(),
                    dto.quantity() != null ? dto.quantity() : 1,
                    dto.note());
        }

        if (dto.productName() == null || dto.productName().isBlank()) {
            throw ApiException.badRequest("productName is required when productId is not provided");
        }
        return new CustomerRequestItem(
                request,
                null,
                dto.productName(),
                null,
                null,
                dto.quantity() != null ? dto.quantity() : 1,
                dto.note());
    }

    private static Specification<CustomerRequest> hasBusinessId(UUID businessId) {
        return (root, query, cb) -> cb.equal(root.get("business").get("id"), businessId);
    }

    private static Specification<CustomerRequest> hasStatus(RequestStatus status) {
        if (status == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<CustomerRequest> hasType(RequestType type) {
        if (type == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    private static Specification<CustomerRequest> hasBranchId(UUID branchId) {
        if (branchId == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("branch").get("id"), branchId);
    }
}
