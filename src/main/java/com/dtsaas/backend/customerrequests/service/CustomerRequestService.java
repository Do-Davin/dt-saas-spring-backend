package com.dtsaas.backend.customerrequests.service;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.repository.BusinessRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.customerrequests.dto.CreateCustomerRequestItemRequest;
import com.dtsaas.backend.customerrequests.dto.CreateCustomerRequestRequest;
import com.dtsaas.backend.customerrequests.dto.PublicRequestResponse;
import com.dtsaas.backend.customerrequests.entity.CustomerRequest;
import com.dtsaas.backend.customerrequests.entity.CustomerRequestItem;
import com.dtsaas.backend.customerrequests.entity.RequestType;
import com.dtsaas.backend.customerrequests.repository.CustomerRequestRepository;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRequestService {

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
}
