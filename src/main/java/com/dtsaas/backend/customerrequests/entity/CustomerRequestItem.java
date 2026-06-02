package com.dtsaas.backend.customerrequests.entity;

import com.dtsaas.backend.products.entity.PricingType;
import com.dtsaas.backend.products.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Items are immutable after creation — no updatedAt column, does not extend BaseTimeEntity.
@Entity
@Table(name = "customer_request_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, updatable = false)
    private CustomerRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name_snapshot", nullable = false)
    private String productNameSnapshot;

    @Column(name = "sales_price_snapshot", precision = 10, scale = 2)
    private BigDecimal salesPriceSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type_snapshot")
    private PricingType pricingTypeSnapshot;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "note")
    private String note;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public CustomerRequestItem(CustomerRequest request, Product product,
                                String productNameSnapshot, BigDecimal salesPriceSnapshot,
                                PricingType pricingTypeSnapshot, int quantity, String note) {
        this.request = request;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.salesPriceSnapshot = salesPriceSnapshot;
        this.pricingTypeSnapshot = pricingTypeSnapshot;
        this.quantity = quantity;
        this.note = note;
    }
}
