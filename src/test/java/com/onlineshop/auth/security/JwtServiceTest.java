package com.onlineshop.auth.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    private final String secret = "b2f7e8c9d1a4e5f6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0";
    private final long accessExp = 1000 * 60 * 10; // 10 min
    private final long refreshExp = 1000 * 60 * 60 * 24 * 7; // 7 days

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, accessExp, refreshExp);
    }

    @Test
    void generateAndValidateAccessToken() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token, userDetails));
        assertEquals("testuser", jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
        assertFalse(jwtService.isRefreshToken(token));
    }

    @Test
    void generateAndValidateRefreshToken() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateRefreshToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token, userDetails));
        assertEquals("testuser", jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.isRefreshToken(token));
    }

    @Test
    void extractClaim() {
        UserDetails userDetails = User.withUsername("claimuser").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(userDetails);
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("claimuser", subject);
    }

    @Test
    void expiredToken() throws InterruptedException {
        JwtService shortLivedJwt = new JwtService(secret, 1, refreshExp);
        UserDetails userDetails = User.withUsername("expuser").password("pass").authorities(Collections.emptyList()).build();
        String token = shortLivedJwt.generateToken(userDetails);
        Thread.sleep(5);
        assertTrue(shortLivedJwt.isTokenExpired(token));
    }

    @Test
    void invalidToken() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(Collections.emptyList()).build();
        String token = "invalid.token.value";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractUsername(token));
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.validateToken(token, userDetails));
    }
}
