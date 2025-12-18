package com.onlineshop.auth.service.impl;

import com.onlineshop.auth.dto.LoginRequest;
import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.dto.RegisterRequest;
import com.onlineshop.auth.dto.ChangePasswordRequest;
import com.onlineshop.auth.model.Role;
import com.onlineshop.auth.model.User;
import com.onlineshop.auth.repository.UserRepository;
import com.onlineshop.auth.repository.RoleRepository;
import com.onlineshop.auth.security.JwtService;
import com.onlineshop.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        Role role = roleRepository.findByName(request.getRole())
            .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));
        user.setRole(role);
        userRepository.save(user);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new RuntimeException("New password must be different from old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthResponse oauth2Login(String email, String name) {
        User user = userRepository.findByUsername(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setPassword(""); // No password for social login
                newUser.setEnabled(true);
                Role makerRole = roleRepository.findByName("MAKER")
                    .orElseThrow(() -> new RuntimeException("Role not found: MAKER"));
                newUser.setRole(makerRole);
                return userRepository.save(newUser);
            });
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }
}
