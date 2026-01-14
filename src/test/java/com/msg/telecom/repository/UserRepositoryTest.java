package com.msg.telecom.repository;

import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ReturnsUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@msgtel.com");
        user.setPasswordHash("password");
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@msgtel.com");
        user.setPasswordHash("password");
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@msgtel.com");
        assertTrue(exists);
    }
}
