package com.onlineshop.auth.controller;

import com.onlineshop.auth.dto.AuthResponse;
import com.onlineshop.auth.dto.ChangePasswordRequest;
import com.onlineshop.auth.dto.LoginRequest;
import com.onlineshop.auth.dto.RegisterRequest;
import com.onlineshop.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_Success() throws Exception {
        AuthResponse response = new AuthResponse("accessToken", "refreshToken");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"Password1!\"}";
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void register_Success() throws Exception {
        AuthResponse response = new AuthResponse("accessToken", "refreshToken");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"Password1!\"," +
                "\"email\":\"test@example.com\"," +
                "\"role\":\"MAKER\"}";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    // @Test
    // void changePassword_Success() throws Exception {
    //     // Use a mock Authentication principal that returns a UserDetails
    //     UserDetails userDetails = mock(UserDetails.class, withSettings().extraInterfaces(java.io.Serializable.class));
    //     when(userDetails.getUsername()).thenReturn("testuser");
    //     when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());
    //     ChangePasswordRequest req = new ChangePasswordRequest();
    //     req.setOldPassword("Password1!");
    //     req.setNewPassword("Password2!");
    //     doNothing().when(authService).changePassword(eq("testuser"), any(ChangePasswordRequest.class));
    //     String json = "{" +
    //             "\"oldPassword\":\"Password1!\"," +
    //             "\"newPassword\":\"Password2!\"}";
    //     mockMvc.perform(post("/api/auth/change-password")
    //             .requestAttr("org.springframework.security.core.Authentication", new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(json))
    //             .andExpect(status().isOk());
    // }

    @Test
    void refreshToken_Success() throws Exception {
        AuthResponse response = new AuthResponse("accessToken", "refreshToken");
        when(authService.refreshToken("refreshTokenValue")).thenReturn(response);
        mockMvc.perform(post("/api/auth/refresh-token")
                .param("refreshToken", "refreshTokenValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }
}
