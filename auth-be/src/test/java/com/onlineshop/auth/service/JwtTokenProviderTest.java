package com.onlineshop.auth.service;

import com.onlineshop.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "b2f7e8c9d1a4e5f6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0";
    private final long expiration = 1000 * 60 * 10; // 10 min

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", expiration);
    }

    @Test
    void generateAndValidateToken() {
        User user = new User();
        user.setUsername("testuser");
        String token = jwtTokenProvider.generateToken(user);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
        assertEquals("testuser", jwtTokenProvider.extractUsername(token));
        assertFalse(jwtTokenProvider.isRefreshToken(token));
    }

    @Test
    void generateAndValidateRefreshToken() {
        User user = new User();
        user.setUsername("refreshuser");
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("refreshuser", jwtTokenProvider.getUsernameFromToken(refreshToken));
        assertTrue(jwtTokenProvider.isRefreshToken(refreshToken));
    }

    @Test
    void invalidToken() {
        String token = "invalid.token.value";
        assertFalse(jwtTokenProvider.validateToken(token));
        assertThrows(Exception.class, () -> jwtTokenProvider.getUsernameFromToken(token));
        assertThrows(Exception.class, () -> jwtTokenProvider.extractUsername(token));
        assertFalse(jwtTokenProvider.isRefreshToken(token));
    }
}
