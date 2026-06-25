package com.dtsaas.backend.sales.entity;

import com.dtsaas.backend.products.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

// Items are immutable after creation — no updatedAt, does not extend BaseTimeEntity.
@Entity
@Table(name = "sale_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false, updatable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name_snapshot", nullable = false)
    private String productNameSnapshot;

    @Column(name = "category_id_snapshot")
    private UUID categoryIdSnapshot;

    @Column(name = "category_name_snapshot")
    private String categoryNameSnapshot;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_sales_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitSalesPrice;

    @Column(name = "unit_cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCostPrice;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "line_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineCost;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SaleItem(Sale sale, Product product, String productNameSnapshot,
                    UUID categoryIdSnapshot, String categoryNameSnapshot,
                    int quantity, BigDecimal unitSalesPrice, BigDecimal unitCostPrice,
                    BigDecimal discountAmount, BigDecimal lineTotal, BigDecimal lineCost) {
        this.sale = sale;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.categoryIdSnapshot = categoryIdSnapshot;
        this.categoryNameSnapshot = categoryNameSnapshot;
        this.quantity = quantity;
        this.unitSalesPrice = unitSalesPrice;
        this.unitCostPrice = unitCostPrice;
        this.discountAmount = discountAmount;
        this.lineTotal = lineTotal;
        this.lineCost = lineCost;
    }
}
