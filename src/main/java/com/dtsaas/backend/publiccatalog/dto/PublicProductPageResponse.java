package com.dtsaas.backend.publiccatalog.dto;

import java.util.List;

public record PublicProductPageResponse(
        List<PublicProductResponse> items,
        Pagination pagination) {

    public record Pagination(int page, int limit, long total, int totalPages) {
    }
}
