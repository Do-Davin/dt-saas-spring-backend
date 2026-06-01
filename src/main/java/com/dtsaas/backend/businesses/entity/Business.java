package com.dtsaas.backend.businesses.entity;

import com.dtsaas.backend.auth.entity.Owner;
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

import java.util.UUID;

@Entity
@Table(name = "businesses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private Owner owner;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "name_km")
    private String nameKm;

    @Setter
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BusinessType type;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_mode", nullable = false)
    private CatalogMode catalogMode = CatalogMode.PRODUCT_CATALOG;

    public Business(Owner owner, String name, String nameKm, String slug, BusinessType type, CatalogMode catalogMode) {
        this.owner = owner;
        this.name = name;
        this.nameKm = nameKm;
        this.slug = slug;
        this.type = type;
        if (catalogMode != null) {
            this.catalogMode = catalogMode;
        }
    }
}
