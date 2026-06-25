package com.dtsaas.backend.products.dto;

import java.util.UUID;

public record ProductPrimaryImageResponse(UUID id, String url, String alt) {
}
