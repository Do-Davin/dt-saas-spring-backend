package com.dtsaas.backend.sales.entity;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false, updatable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "sale_date", nullable = false)
    private Instant saleDate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "profit", nullable = false, precision = 12, scale = 2)
    private BigDecimal profit;

    @Column(name = "note")
    private String note;

    @Formula("(SELECT COUNT(si.id) FROM sale_items si WHERE si.sale_id = id)")
    private int itemCount;

    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<SaleItem> items = new ArrayList<>();

    public Sale(Business business, Branch branch, Instant saleDate,
                BigDecimal totalAmount, BigDecimal totalCost, BigDecimal profit,
                String note) {
        this.business = business;
        this.branch = branch;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.totalCost = totalCost;
        this.profit = profit;
        this.note = note;
    }

    public void addItem(SaleItem item) {
        items.add(item);
    }
}
