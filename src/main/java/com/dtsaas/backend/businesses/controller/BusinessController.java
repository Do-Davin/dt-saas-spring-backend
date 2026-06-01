package com.dtsaas.backend.businesses.controller;

import com.dtsaas.backend.businesses.dto.BusinessResponse;
import com.dtsaas.backend.businesses.dto.CreateBusinessRequest;
import com.dtsaas.backend.businesses.service.BusinessService;
import com.dtsaas.backend.common.security.AuthenticatedOwner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessResponse create(
            @AuthenticationPrincipal AuthenticatedOwner owner,
            @Valid @RequestBody CreateBusinessRequest request) {
        return businessService.create(owner.id(), request);
    }

    @GetMapping
    public List<BusinessResponse> list(@AuthenticationPrincipal AuthenticatedOwner owner) {
        return businessService.listForOwner(owner.id());
    }
}
