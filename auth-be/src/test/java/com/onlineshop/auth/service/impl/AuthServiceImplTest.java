package com.onlineshop.auth.service.impl;

import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.dto.RegisterRequest;
import com.onlineshop.auth.model.Role;
import com.onlineshop.auth.model.User;
import com.onlineshop.auth.repository.RoleRepository;
import com.onlineshop.auth.repository.UserRepository;
import com.onlineshop.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password1!");
        request.setEmail("test@example.com");
        request.setRole("MAKER");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        Role role = new Role();
        role.setId(1L);
        role.setName("MAKER");
        when(roleRepository.findByName("MAKER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthResponse response = authService.register(request);
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void register_RoleNotFound_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password1!");
        request.setEmail("test@example.com");
        request.setRole("UNKNOWN");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("Role not found"));
    }
}
