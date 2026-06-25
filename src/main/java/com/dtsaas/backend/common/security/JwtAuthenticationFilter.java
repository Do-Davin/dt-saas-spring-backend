package com.dtsaas.backend.common.security;

import com.dtsaas.backend.auth.entity.Owner;
import com.dtsaas.backend.auth.repository.OwnerRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final OwnerRepository ownerRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(BEARER_PREFIX.length());
            try {
                UUID ownerId = jwtService.extractOwnerId(token);
                Optional<Owner> owner = ownerRepository.findById(ownerId);
                owner.ifPresent(o -> {
                    AuthenticatedOwner principal = new AuthenticatedOwner(
                            o.getId(), o.getEmail(), o.getUsername(), o.getName(), o.getRole());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    principal, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            } catch (JwtException | IllegalArgumentException ignored) {
                // Invalid token: leave context unauthenticated; SecurityFilterChain will 401.
            }
        }

        chain.doFilter(request, response);
    }
}
