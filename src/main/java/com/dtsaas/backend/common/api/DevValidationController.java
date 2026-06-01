package com.dtsaas.backend.common.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dev")
public class DevValidationController {

    @PostMapping("/validation-test")
    public Map<String, Object> validationTest(@Valid @RequestBody ValidationTestRequest request) {
        return Map.of(
                "status", "ok",
                "message", "Validation passed");
    }
}
