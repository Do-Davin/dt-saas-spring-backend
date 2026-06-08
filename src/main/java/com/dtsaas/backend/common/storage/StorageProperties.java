package com.dtsaas.backend.common.storage;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.storage")
@Validated
public record StorageProperties(
                @NotBlank String endpoint,
                @NotBlank String region,
                @NotBlank String bucket,
                @NotBlank String accessKey,
                @NotBlank String secretKey,
                boolean autoCreateBucket) {
}
