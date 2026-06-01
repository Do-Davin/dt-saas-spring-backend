package com.dtsaas.backend.auth.controller;

import com.dtsaas.backend.auth.dto.AuthOwnerResponse;
import com.dtsaas.backend.auth.dto.LoginRequest;
import com.dtsaas.backend.auth.dto.RegisterRequest;
import com.dtsaas.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthOwnerResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthOwnerResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
