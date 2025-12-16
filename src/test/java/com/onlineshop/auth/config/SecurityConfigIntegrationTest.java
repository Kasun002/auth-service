package com.onlineshop.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import com.onlineshop.auth.controller.AuthController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.onlineshop.auth.service.AuthService;
import com.onlineshop.auth.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    void loginEndpoint_RejectsGet() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
               .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePassword_RequiresAuth() throws Exception {
        mockMvc.perform(get("/api/auth/change-password"))
               .andExpect(status().isMethodNotAllowed()); // 405 expected for GET
    }
}
