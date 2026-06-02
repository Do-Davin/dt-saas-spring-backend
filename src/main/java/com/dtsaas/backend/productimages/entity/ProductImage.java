package com.dtsaas.backend.productimages.entity;

import com.dtsaas.backend.common.entity.BaseTimeEntity;
import com.dtsaas.backend.products.entity.Product;
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
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    // "key" is a JPQL reserved word; mapped to the "key" column via @Column
    @Column(name = "key", nullable = false, unique = true)
    private String objectKey;

    @Setter
    @Column(name = "url")
    private String url;

    @Setter
    @Column(name = "alt")
    private String alt;

    @Setter
    @Column(name = "position", nullable = false)
    private int position;

    @Setter
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    public ProductImage(Product product, String objectKey, String url, String alt, int position, boolean isPrimary) {
        this.product = product;
        this.objectKey = objectKey;
        this.url = url;
        this.alt = alt;
        this.position = position;
        this.isPrimary = isPrimary;
    }
}
