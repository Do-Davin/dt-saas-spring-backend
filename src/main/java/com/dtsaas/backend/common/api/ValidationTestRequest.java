package com.dtsaas.backend.common.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidationTestRequest(
                @NotBlank @Email String email,
                @NotBlank String name,
                @NotNull @Min(1) Integer quantity) {
}
