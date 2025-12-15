package com.onlineshop.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.onlineshop.auth.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;
    private final long jwtRefreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration:86400000}") long jwtExpirationMs,
            @Value("${jwt.refreshExpiration:604800000}") long jwtRefreshExpirationMs
    ) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails.getUsername(), jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails.getUsername(), jwtRefreshExpirationMs);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user.getUsername(), jwtExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user.getUsername(), jwtRefreshExpirationMs);
    }

    private String generateToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the given token is a refresh token by comparing its expiration duration.
     * This is a heuristic based on the configured refresh token expiration time.
     * For production, consider using a custom claim to distinguish refresh tokens.
     */
    public boolean isRefreshToken(String token) {
        try {
            Date issuedAt = extractAllClaims(token).getIssuedAt();
            Date expiration = extractExpiration(token);
            long duration = expiration.getTime() - issuedAt.getTime();
            // 1 minute tolerance
            return Math.abs(duration - jwtRefreshExpirationMs) < 60_000;
        } catch (Exception e) {
            return false;
        }
    }

    public SecretKey getJwtSecretKey() {
        return jwtSecretKey;
    }
}
