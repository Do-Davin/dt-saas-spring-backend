package com.dtsaas.backend.sales.repository;

import com.dtsaas.backend.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID>, JpaSpecificationExecutor<Sale> {

    Optional<Sale> findByIdAndBusinessId(UUID id, UUID businessId);

    // ── Report projection ─────────────────────────────────────────────────────

    interface SalesSummaryRow {
        BigDecimal getTotalRevenue();
        BigDecimal getTotalCost();
        BigDecimal getTotalProfit();
        Long getSaleCount();
    }

    @Query(nativeQuery = true, value = """
            SELECT COALESCE(SUM(total_amount), 0) AS totalRevenue,
                   COALESCE(SUM(total_cost), 0)   AS totalCost,
                   COALESCE(SUM(profit), 0)        AS totalProfit,
                   COUNT(id)                       AS saleCount
            FROM   sales
            WHERE  business_id = :businessId
              AND  (:from     IS NULL OR sale_date >= :from)
              AND  (:to       IS NULL OR sale_date <= :to)
              AND  (:branchId IS NULL OR branch_id = :branchId)
            """)
    SalesSummaryRow findSalesSummary(
            @Param("businessId") UUID businessId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("branchId") UUID branchId);
}
