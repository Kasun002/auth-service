package com.onlineshop.auth.controller;

import com.onlineshop.auth.dto.LoginRequest;
import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.dto.RegisterRequest;
import com.onlineshop.auth.dto.ChangePasswordRequest;
import com.onlineshop.auth.service.AuthService;
import com.onlineshop.auth.security.JwtService;
import com.onlineshop.auth.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Operation(summary = "User login", description = "Authenticate user with username and password. Returns JWT token.", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or request")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "User registration", description = "Register a new user. Returns JWT token.", responses = {
            @ApiResponse(responseCode = "200", description = "Registration successful, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Change user password", description = "Allows an authenticated user to change their password. Requires JWT authentication.", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or old password incorrect"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required")
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Refresh JWT tokens", description = "Obtain new access and refresh tokens using a valid refresh token.", responses = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<AuthResponse> oauth2Success(@AuthenticationPrincipal OidcUser oidcUser,
                                                      @AuthenticationPrincipal OAuth2User oauth2User) {
        String email = null;
        String name = null;
        if (oidcUser != null) {
            email = oidcUser.getEmail();
            name = oidcUser.getFullName();
        } else if (oauth2User != null) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
        }
        if (email == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.oauth2Login(email, name));
    }
}
