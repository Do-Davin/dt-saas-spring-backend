package com.dtsaas.backend.publiccatalog.dto;

import java.util.List;

public record PublicCatalogResponse(
                PublicBusinessResponse business,
                List<PublicBranchResponse> branches,
                List<PublicCategoryResponse> categories) {
}
