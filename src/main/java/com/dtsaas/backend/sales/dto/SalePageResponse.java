package com.dtsaas.backend.sales.dto;

import java.util.List;

public record SalePageResponse(
        List<SaleListItemResponse> items,
        Pagination pagination) {

    public record Pagination(int page, int size, long total, int totalPages) {}
}
