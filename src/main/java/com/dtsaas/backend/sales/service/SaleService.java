package com.dtsaas.backend.sales.service;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.branches.repository.BranchRepository;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.products.entity.Product;
import com.dtsaas.backend.products.repository.ProductRepository;
import com.dtsaas.backend.sales.dto.CreateSaleItemRequest;
import com.dtsaas.backend.sales.dto.CreateSaleRequest;
import com.dtsaas.backend.sales.dto.SaleListItemResponse;
import com.dtsaas.backend.sales.dto.SalePageResponse;
import com.dtsaas.backend.sales.dto.SaleResponse;
import com.dtsaas.backend.sales.entity.Sale;
import com.dtsaas.backend.sales.entity.SaleItem;
import com.dtsaas.backend.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final BusinessService businessService;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public SaleResponse create(UUID businessId, UUID ownerId, CreateSaleRequest request) {
        Business business = businessService.requireOwnedBusiness(businessId, ownerId);

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findByIdAndBusinessId(request.branchId(), businessId)
                    .orElseThrow(() -> ApiException.notFound("Branch not found in this business"));
        }

        Instant saleDate = request.saleDate() != null ? request.saleDate() : Instant.now();

        // ── Validate all items and resolve prices before mutating state ──────────

        record ResolvedItem(
                Product product,
                String productNameSnapshot,
                UUID categoryIdSnapshot,
                String categoryNameSnapshot,
                int quantity,
                BigDecimal unitSalesPrice,
                BigDecimal unitCostPrice,
                BigDecimal discountAmount,
                BigDecimal lineTotal,
                BigDecimal lineCost) {}

        List<ResolvedItem> resolved = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CreateSaleItemRequest itemReq : request.items()) {
            Product product = productRepository
                    .findByIdAndBusinessIdAndDeletedAtIsNull(itemReq.productId(), businessId)
                    .orElseThrow(() -> ApiException.notFound(
                            "Product not found: " + itemReq.productId()));

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw ApiException.badRequest(
                        "Insufficient stock for \"" + product.getName() + "\". "
                        + "Available: " + product.getStockQuantity()
                        + ", requested: " + itemReq.quantity());
            }

            BigDecimal unitSalesPrice = itemReq.unitSalesPrice() != null
                    ? itemReq.unitSalesPrice()
                    : (product.getSalesPrice() != null ? product.getSalesPrice() : BigDecimal.ZERO);
            BigDecimal unitCostPrice = itemReq.unitCostPrice() != null
                    ? itemReq.unitCostPrice()
                    : (product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO);
            BigDecimal discountAmount = itemReq.discountAmount() != null
                    ? itemReq.discountAmount()
                    : BigDecimal.ZERO;

            BigDecimal lineTotal = unitSalesPrice
                    .subtract(discountAmount)
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));
            BigDecimal lineCost = unitCostPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            UUID categoryIdSnapshot = null;
            String categoryNameSnapshot = null;
            if (product.getCategory() != null) {
                categoryIdSnapshot = product.getCategory().getId();
                categoryNameSnapshot = product.getCategory().getName();
            }

            resolved.add(new ResolvedItem(
                    product, product.getName(),
                    categoryIdSnapshot, categoryNameSnapshot,
                    itemReq.quantity(), unitSalesPrice, unitCostPrice,
                    discountAmount, lineTotal, lineCost));

            totalAmount = totalAmount.add(lineTotal);
            totalCost = totalCost.add(lineCost);
        }

        BigDecimal profit = totalAmount.subtract(totalCost);

        // ── Persist Sale + SaleItems, decrement stock ─────────────────────────────

        Sale sale = new Sale(business, branch, saleDate, totalAmount, totalCost, profit,
                request.note());

        for (ResolvedItem r : resolved) {
            SaleItem item = new SaleItem(
                    sale, r.product(), r.productNameSnapshot(),
                    r.categoryIdSnapshot(), r.categoryNameSnapshot(),
                    r.quantity(), r.unitSalesPrice(), r.unitCostPrice(),
                    r.discountAmount(), r.lineTotal(), r.lineCost());
            sale.addItem(item);
            r.product().setStockQuantity(r.product().getStockQuantity() - r.quantity());
        }

        return SaleResponse.from(saleRepository.save(sale));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public SalePageResponse list(UUID businessId, UUID ownerId,
                                 Instant from, Instant to, UUID branchId,
                                 int page, int size) {
        businessService.requireOwnedBusiness(businessId, ownerId);

        Specification<Sale> spec = Specification.where(hasBusinessId(businessId));
        if (from != null)     spec = spec.and(fromDate(from));
        if (to != null)       spec = spec.and(toDate(to));
        if (branchId != null) spec = spec.and(hasBranchId(branchId));

        Page<Sale> resultPage = saleRepository.findAll(
                spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate")));

        List<SaleListItemResponse> items = resultPage.stream()
                .map(SaleListItemResponse::from)
                .toList();

        return new SalePageResponse(
                items,
                new SalePageResponse.Pagination(
                        page, size, resultPage.getTotalElements(), resultPage.getTotalPages()));
    }

    @Transactional(readOnly = true)
    public SaleResponse getOne(UUID businessId, UUID saleId, UUID ownerId) {
        businessService.requireOwnedBusiness(businessId, ownerId);
        Sale sale = saleRepository.findByIdAndBusinessId(saleId, businessId)
                .orElseThrow(() -> ApiException.notFound("Sale not found"));
        return SaleResponse.from(sale);
    }

    // ── Specifications ────────────────────────────────────────────────────────

    private static Specification<Sale> hasBusinessId(UUID businessId) {
        return (root, query, cb) -> cb.equal(root.get("business").get("id"), businessId);
    }

    private static Specification<Sale> fromDate(Instant from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("saleDate"), from);
    }

    private static Specification<Sale> toDate(Instant to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("saleDate"), to);
    }

    private static Specification<Sale> hasBranchId(UUID branchId) {
        return (root, query, cb) -> cb.equal(root.get("branch").get("id"), branchId);
    }
}
