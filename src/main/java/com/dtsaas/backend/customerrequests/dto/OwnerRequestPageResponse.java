package com.dtsaas.backend.customerrequests.dto;

import java.util.List;

public record OwnerRequestPageResponse(
        List<OwnerRequestListItemResponse> items,
        Pagination pagination) {

    public record Pagination(int page, int limit, long total, int totalPages) {}
}
