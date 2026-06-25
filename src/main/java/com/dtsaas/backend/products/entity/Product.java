package com.dtsaas.backend.products.entity;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.categories.entity.Category;
import com.dtsaas.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false, updatable = false)
    private Business business;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "name_km")
    private String nameKm;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "description_km")
    private String descriptionKm;

    @Setter
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Setter
    @Column(name = "sales_price", precision = 12, scale = 2)
    private BigDecimal salesPrice;

    @Setter
    @Column(name = "discount", precision = 12, scale = 2)
    private BigDecimal discount;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    private PricingType pricingType = PricingType.FIXED;

    @Setter
    @Column(name = "label")
    private String label;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "uom", nullable = false)
    private UnitOfMeasure uom = UnitOfMeasure.UNIT;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "toppings", columnDefinition = "jsonb")
    private Map<String, Object> toppings;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients", columnDefinition = "jsonb")
    private Map<String, Object> ingredients;

    @Setter
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Setter
    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true;

    @Setter
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    @Setter
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Setter
    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Product(Business business, Branch branch, Category category,
                   String name, String nameKm, String description, String descriptionKm,
                   BigDecimal purchasePrice, BigDecimal salesPrice, BigDecimal discount,
                   PricingType pricingType, String label, UnitOfMeasure uom,
                   boolean isAvailable, boolean isVisible) {
        this.business = business;
        this.branch = branch;
        this.category = category;
        this.name = name;
        this.nameKm = nameKm;
        this.description = description;
        this.descriptionKm = descriptionKm;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
        this.discount = discount;
        this.pricingType = pricingType != null ? pricingType : PricingType.FIXED;
        this.label = label;
        this.uom = uom != null ? uom : UnitOfMeasure.UNIT;
        this.isAvailable = isAvailable;
        this.isVisible = isVisible;
    }
}
