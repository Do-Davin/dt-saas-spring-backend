package com.dtsaas.backend.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService(new BCryptPasswordEncoder(4));

    @Test
    void hashReturnsValueDifferentFromRawPassword() {
        String raw = "s3cret!";
        String hash = passwordService.hash(raw);
        assertNotNull(hash);
        assertNotEquals(raw, hash);
    }

    @Test
    void matchesReturnsTrueForCorrectPassword() {
        String raw = "s3cret!";
        String hash = passwordService.hash(raw);
        assertTrue(passwordService.matches(raw, hash));
    }

    @Test
    void matchesReturnsFalseForWrongPassword() {
        String raw = "s3cret!";
        String hash = passwordService.hash(raw);
        assertFalse(passwordService.matches("wrong", hash));
    }
}
