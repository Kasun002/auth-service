package com.onlineshop.auth.security;

import com.onlineshop.auth.model.Role;
import com.onlineshop.auth.model.User;
import com.onlineshop.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        user.setEnabled(true);
        Role role = new Role();
        role.setName("MAKER");
        user.setRole(role);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("MAKER")));
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nouser"));
    }

    @Test
    void loadUserByUsername_UserDisabled() {
        User user = new User();
        user.setUsername("disableduser");
        user.setPassword("encodedPass");
        user.setEnabled(false);
        Role role = new Role();
        role.setName("MAKER");
        user.setRole(role);
        when(userRepository.findByUsername("disableduser")).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername("disableduser");
        assertFalse(userDetails.isAccountNonLocked());
    }
}
