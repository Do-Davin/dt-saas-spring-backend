package com.dtsaas.backend.auth.service;

import com.dtsaas.backend.auth.dto.AuthOwnerResponse;
import com.dtsaas.backend.auth.dto.AuthResponse;
import com.dtsaas.backend.auth.dto.LoginRequest;
import com.dtsaas.backend.auth.dto.RegisterRequest;
import com.dtsaas.backend.auth.entity.Owner;
import com.dtsaas.backend.auth.repository.OwnerRepository;
import com.dtsaas.backend.common.exception.ApiException;
import com.dtsaas.backend.common.security.JwtService;
import com.dtsaas.backend.common.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final OwnerRepository ownerRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (ownerRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Email already registered");
        }

        Owner owner = new Owner(
                request.email(),
                passwordService.hash(request.password()),
                request.name());

        return toAuthResponse(ownerRepository.save(owner));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Owner owner = ownerRepository.findByEmail(request.email())
                .orElseThrow(() -> ApiException.unauthorized(INVALID_CREDENTIALS));

        if (!passwordService.matches(request.password(), owner.getPasswordHash())) {
            throw ApiException.unauthorized(INVALID_CREDENTIALS);
        }

        return toAuthResponse(owner);
    }

    private AuthResponse toAuthResponse(Owner owner) {
        String token = jwtService.generate(owner.getId(), owner.getEmail(), owner.getName());
        return new AuthResponse(AuthOwnerResponse.from(owner), token);
    }
}
