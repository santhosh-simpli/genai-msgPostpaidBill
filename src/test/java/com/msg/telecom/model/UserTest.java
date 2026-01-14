package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedPassword123");
        user.setEmail("test@msgtel.com");
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals("test@msgtel.com", user.getEmail());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testUserDetails_GetPassword() {
        assertEquals("hashedPassword123", user.getPassword());
    }

    @Test
    void testUserDetails_GetUsername() {
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testUserDetails_GetAuthorities_Customer() {
        user.setRole(UserRole.CUSTOMER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void testUserDetails_GetAuthorities_Admin() {
        user.setRole(UserRole.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDetails_GetAuthorities_Operator() {
        user.setRole(UserRole.OPERATOR);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR")));
    }

    @Test
    void testUserDetails_IsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testUserDetails_IsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testUserDetails_IsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testUserDetails_IsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testEquality() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("testuser");
        user1.setEmail("test@msgtel.com");

        User user2 = new User();
        user2.setUserId(1L);
        user2.setUsername("testuser");
        user2.setEmail("test@msgtel.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testInequality() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("user2");

        assertNotEquals(user1, user2);
    }

    @Test
    void testToString() {
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@msgtel.com"));
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNull(emptyUser.getUserId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User fullUser = new User(1L, "fulluser", "password", "full@msgtel.com", UserRole.ADMIN, now);
        
        assertEquals(1L, fullUser.getUserId());
        assertEquals("fulluser", fullUser.getUsername());
        assertEquals("password", fullUser.getPasswordHash());
        assertEquals("full@msgtel.com", fullUser.getEmail());
        assertEquals(UserRole.ADMIN, fullUser.getRole());
        assertEquals(now, fullUser.getCreatedAt());
    }

    @Test
    void testBuilder() {
        User builtUser = User.builder()
                .userId(2L)
                .username("builder")
                .passwordHash("builderPass")
                .email("builder@msgtel.com")
                .role(UserRole.OPERATOR)
                .build();
        
        assertEquals(2L, builtUser.getUserId());
        assertEquals("builder", builtUser.getUsername());
        assertEquals("builderPass", builtUser.getPasswordHash());
        assertEquals("builder@msgtel.com", builtUser.getEmail());
        assertEquals(UserRole.OPERATOR, builtUser.getRole());
    }

    @Test
    void testOnCreate() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPasswordHash("pass");
        newUser.setEmail("new@msgtel.com");
        newUser.setRole(UserRole.CUSTOMER);
        
        // Simulate @PrePersist
        newUser.onCreate();
        
        assertNotNull(newUser.getCreatedAt());
    }

    @Test
    void testNullValues() {
        User nullUser = new User();
        assertNull(nullUser.getUserId());
        assertNull(nullUser.getUsername());
        assertNull(nullUser.getPasswordHash());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getCreatedAt());
    }

    @Test
    void testSetters() {
        User testUser = new User();
        testUser.setUserId(100L);
        testUser.setUsername("newUsername");
        testUser.setPasswordHash("newPassword");
        testUser.setEmail("new@email.com");
        testUser.setRole(UserRole.ADMIN);
        
        assertEquals(100L, testUser.getUserId());
        assertEquals("newUsername", testUser.getUsername());
        assertEquals("newPassword", testUser.getPasswordHash());
        assertEquals("new@email.com", testUser.getEmail());
        assertEquals(UserRole.ADMIN, testUser.getRole());
    }
}
