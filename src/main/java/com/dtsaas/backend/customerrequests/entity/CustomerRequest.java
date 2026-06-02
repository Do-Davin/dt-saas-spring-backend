package com.dtsaas.backend.customerrequests.entity;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerRequest extends BaseTimeEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private RequestType type;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.NEW;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_note")
    private String customerNote;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CustomerRequestItem> items = new ArrayList<>();

    public CustomerRequest(Business business, Branch branch, RequestType type,
                           String customerName, String customerPhone, String customerNote) {
        this.business = business;
        this.branch = branch;
        this.type = type;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerNote = customerNote;
    }

    public void addItem(CustomerRequestItem item) {
        items.add(item);
    }
}
