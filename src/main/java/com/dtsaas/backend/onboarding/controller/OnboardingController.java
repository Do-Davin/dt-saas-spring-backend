package com.dtsaas.backend.onboarding.controller;

import com.dtsaas.backend.onboarding.dto.OnboardingRequest;
import com.dtsaas.backend.onboarding.dto.OnboardingResponse;
import com.dtsaas.backend.onboarding.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OnboardingResponse register(@Valid @RequestBody OnboardingRequest request) {
        return onboardingService.register(request);
    }
}
