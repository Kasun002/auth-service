package com.onlineshop.auth.service.impl;

import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.model.User;
import com.onlineshop.auth.repository.UserRepository;
import com.onlineshop.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplRefreshTokenTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void refreshToken_Success() {
        String refreshToken = "validRefreshToken";
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("newRefreshToken");

        AuthResponse response = authService.refreshToken(refreshToken);
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        String refreshToken = "invalidToken";
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshToken));
        assertTrue(ex.getMessage().contains("Invalid refresh token"));
    }

    @Test
    void refreshToken_UserNotFound_ThrowsException() {
        String refreshToken = "validRefreshToken";
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("nouser");
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshToken));
        assertTrue(ex.getMessage().contains("User not found"));
    }
}
