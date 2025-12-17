package com.onlineshop.auth.service.impl;

import com.onlineshop.auth.dto.ChangePasswordRequest;
import com.onlineshop.auth.model.User;
import com.onlineshop.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplChangePasswordTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changePassword_Success() {
        String username = "testuser";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass1!");
        request.setNewPassword("newPass2!");
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedOldPass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass1!", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass2!")).thenReturn("encodedNewPass");

        assertDoesNotThrow(() -> authService.changePassword(username, request));
        verify(userRepository).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        String username = "nouser";
        ChangePasswordRequest request = new ChangePasswordRequest();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(username, request));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void changePassword_OldPasswordIncorrect_ThrowsException() {
        String username = "testuser";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOld");
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedOldPass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "encodedOldPass")).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(username, request));
        assertTrue(ex.getMessage().contains("Old password is incorrect"));
    }

    @Test
    void changePassword_NewPasswordSameAsOld_ThrowsException() {
        String username = "testuser";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass1!");
        request.setNewPassword("oldPass1!");
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedOldPass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass1!", "encodedOldPass")).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(username, request));
        assertTrue(ex.getMessage().contains("New password must be different from old password"));
    }
}
