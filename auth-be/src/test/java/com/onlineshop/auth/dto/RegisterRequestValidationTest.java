package com.onlineshop.auth.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestValidationTest {
    private final Validator validator;

    public RegisterRequestValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validPassword_PassesValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("Password1!");
        req.setEmail("test@example.com");
        req.setRole("MAKER");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidPassword_FailsValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("short");
        req.setEmail("test@example.com");
        req.setRole("MAKER");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 8 characters")));
    }

    @Test
    void passwordMissingUppercase_FailsValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("password1!");
        req.setEmail("test@example.com");
        req.setRole("MAKER");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void passwordMissingDigit_FailsValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("Password!");
        req.setEmail("test@example.com");
        req.setRole("MAKER");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void passwordMissingSpecial_FailsValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("Password1");
        req.setEmail("test@example.com");
        req.setRole("MAKER");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }
}
