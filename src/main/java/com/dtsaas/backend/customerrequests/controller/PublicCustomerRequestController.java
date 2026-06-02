package com.dtsaas.backend.customerrequests.controller;

import com.dtsaas.backend.customerrequests.dto.CreateCustomerRequestRequest;
import com.dtsaas.backend.customerrequests.dto.PublicRequestResponse;
import com.dtsaas.backend.customerrequests.service.CustomerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/{businessSlug}/requests")
@RequiredArgsConstructor
public class PublicCustomerRequestController {

    private final CustomerRequestService customerRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicRequestResponse submit(
            @PathVariable String businessSlug,
            @RequestBody @Valid CreateCustomerRequestRequest dto) {
        return customerRequestService.submitRequest(businessSlug, dto);
    }
}
