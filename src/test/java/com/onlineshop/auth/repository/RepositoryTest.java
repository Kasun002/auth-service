package com.onlineshop.auth.repository;

import com.onlineshop.auth.model.Role;
import com.onlineshop.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByUsername_UserNotFound() {
        Optional<User> user = userRepository.findByUsername("nouser");
        assertTrue(user.isEmpty());
    }

    @Test
    void findByName_RoleNotFound() {
        Optional<Role> role = roleRepository.findByName("NOROLE");
        assertTrue(role.isEmpty());
    }
}
