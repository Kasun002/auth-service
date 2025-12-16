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
    void extractClaimWithCustomClaim() {
        UserDetails userDetails = User.withUsername("claimuser2").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(userDetails);
        Claims claims = jwtService.extractClaim(token, c -> c);
        assertEquals("claimuser2", claims.getSubject());
        assertNotNull(claims.getExpiration());
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
    void extractExpirationCoversException() {
        String token = "invalid.token.value";
        // isTokenExpired should return true if exception is thrown
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void invalidToken() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(Collections.emptyList()).build();
        String token = "invalid.token.value";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractUsername(token));
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.validateToken(token, userDetails));
    }

    @Test
    void generateTokenAndRefreshTokenWithUserEntity() {
        com.onlineshop.auth.model.User user = new com.onlineshop.auth.model.User();
        user.setUsername("entityuser");
        user.setPassword("pass");
        user.setEnabled(true);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertEquals("entityuser", jwtService.extractUsername(accessToken));
        assertEquals("entityuser", jwtService.extractUsername(refreshToken));
        assertFalse(jwtService.isTokenExpired(accessToken));
        assertFalse(jwtService.isTokenExpired(refreshToken));
        assertFalse(jwtService.isRefreshToken(accessToken));
        assertTrue(jwtService.isRefreshToken(refreshToken));
    }

    @Test
    void validateTokenWithString() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void extractUsernameWithString() {
        UserDetails userDetails = User.withUsername("extractuser").password("pass").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(userDetails);
        assertEquals("extractuser", jwtService.extractUsername(token));
    }

    @Test
    void getJwtSecretKeyCoverage() {
        assertNotNull(jwtService.getJwtSecretKey());
    }

    @Test
    void isRefreshTokenExceptionBranch() {
        // This token will throw an exception in isRefreshToken, which should return false
        String token = "invalid.token.value";
        assertFalse(jwtService.isRefreshToken(token));
    }

    @Test
    void extractAllClaimsExceptionBranch() {
        // This will throw in extractAllClaims, which is used by extractClaim
        String token = "invalid.token.value";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractClaim(token, Claims::getSubject));
    }
}
