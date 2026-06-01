package com.dtsaas.backend.common.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "test-secret-please-ignore-must-be-at-least-256-bits-aaaaaaaaaaaa";
    private final JwtService jwtService = new JwtService(SECRET, 3600);

    @Test
    void generateProducesNonEmptyCompactJws() {
        String token = jwtService.generate(UUID.randomUUID(), "owner@test.com", "Owner");
        assertNotNull(token);
        assertEquals(2, token.chars().filter(c -> c == '.').count());
    }

    @Test
    void extractOwnerIdReturnsSubjectFromGeneratedToken() {
        UUID ownerId = UUID.randomUUID();
        String token = jwtService.generate(ownerId, "owner@test.com", "Owner");
        assertEquals(ownerId, jwtService.extractOwnerId(token));
    }

    @Test
    void parsingTamperedTokenThrows() {
        String token = jwtService.generate(UUID.randomUUID(), "owner@test.com", "Owner");
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThrows(JwtException.class, () -> jwtService.extractOwnerId(tampered));
    }

    @Test
    void parsingTokenSignedWithDifferentKeyThrows() {
        String token = jwtService.generate(UUID.randomUUID(), "owner@test.com", "Owner");
        JwtService other = new JwtService(
                "different-secret-also-256-bits-long-bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", 3600);
        assertThrows(JwtException.class, () -> other.extractOwnerId(token));
    }

    @Test
    void generatesDistinctTokensAcrossCalls() {
        UUID ownerId = UUID.randomUUID();
        String a = jwtService.generate(ownerId, "owner@test.com", "Owner");
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String b = jwtService.generate(ownerId, "owner@test.com", "Owner");
        assertNotEquals(a, b);
        assertTrue(a.split("\\.")[0].equals(b.split("\\.")[0]));
        assertDoesNotThrow(() -> jwtService.extractOwnerId(a));
        assertDoesNotThrow(() -> jwtService.extractOwnerId(b));
    }
}
