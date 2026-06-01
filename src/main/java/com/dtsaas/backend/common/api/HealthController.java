package com.dtsaas.backend.common.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "dt-saas-backend",
                "timestamp", Instant.now().toString());
    }

}
