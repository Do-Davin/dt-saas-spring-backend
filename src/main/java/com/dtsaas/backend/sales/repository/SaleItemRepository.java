package com.dtsaas.backend.sales.repository;

import com.dtsaas.backend.sales.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

    // ── Report projections ────────────────────────────────────────────────────

    interface CategoryRevenueRow {
        UUID getCategoryId();
        String getCategoryName();
        BigDecimal getTotalRevenue();
        BigDecimal getTotalCost();
        Long getQuantity();
    }

    interface ProductRevenueRow {
        UUID getProductId();
        String getProductName();
        BigDecimal getTotalRevenue();
        BigDecimal getTotalCost();
        Long getQuantity();
    }

    @Query(nativeQuery = true, value = """
            SELECT si.category_id_snapshot   AS categoryId,
                   si.category_name_snapshot AS categoryName,
                   SUM(si.line_total)        AS totalRevenue,
                   SUM(si.line_cost)         AS totalCost,
                   SUM(si.quantity)          AS quantity
            FROM   sale_items si
            JOIN   sales s ON si.sale_id = s.id
            WHERE  s.business_id = :businessId
              AND  (:from     IS NULL OR s.sale_date >= :from)
              AND  (:to       IS NULL OR s.sale_date <= :to)
              AND  (:branchId IS NULL OR s.branch_id = :branchId)
            GROUP  BY si.category_id_snapshot, si.category_name_snapshot
            ORDER  BY SUM(si.line_total) DESC
            """)
    List<CategoryRevenueRow> findRevenueByCategory(
            @Param("businessId") UUID businessId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("branchId") UUID branchId);

    @Query(nativeQuery = true, value = """
            SELECT si.product_id              AS productId,
                   si.product_name_snapshot   AS productName,
                   SUM(si.line_total)         AS totalRevenue,
                   SUM(si.line_cost)          AS totalCost,
                   SUM(si.quantity)           AS quantity
            FROM   sale_items si
            JOIN   sales s ON si.sale_id = s.id
            WHERE  s.business_id = :businessId
              AND  (:from     IS NULL OR s.sale_date >= :from)
              AND  (:to       IS NULL OR s.sale_date <= :to)
              AND  (:branchId IS NULL OR s.branch_id = :branchId)
            GROUP  BY si.product_id, si.product_name_snapshot
            ORDER  BY SUM(si.line_total) DESC
            """)
    List<ProductRevenueRow> findRevenueByProduct(
            @Param("businessId") UUID businessId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("branchId") UUID branchId);
}
