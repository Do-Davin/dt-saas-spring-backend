package com.dtsaas.backend.categories.entity;

import com.dtsaas.backend.branches.entity.Branch;
import com.dtsaas.backend.businesses.entity.Business;
import com.dtsaas.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

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
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "name_km")
    private String nameKm;

    @Setter
    @Column(name = "position", nullable = false)
    private int position;

    @Setter
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Category(Business business, Branch branch, String name, String nameKm, Integer position, Boolean isActive) {
        this.business = business;
        this.branch = branch;
        this.name = name;
        this.nameKm = nameKm;
        this.position = position != null ? position : 0;
        this.isActive = isActive == null || isActive;
    }
}
