package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    private User user;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedpassword123");
        user.setEmail("test@msgtel.com");
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(testTime);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword123", user.getPasswordHash());
        assertEquals("test@msgtel.com", user.getEmail());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(testTime, user.getCreatedAt());
    }

    @Test
    void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();
        User builtUser = User.builder()
                .userId(2L)
                .username("admin")
                .passwordHash("adminpass")
                .email("admin@msgtel.com")
                .role(UserRole.ADMIN)
                .createdAt(now)
                .build();

        assertEquals(2L, builtUser.getUserId());
        assertEquals("admin", builtUser.getUsername());
        assertEquals("adminpass", builtUser.getPasswordHash());
        assertEquals("admin@msgtel.com", builtUser.getEmail());
        assertEquals(UserRole.ADMIN, builtUser.getRole());
        assertEquals(now, builtUser.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User constructedUser = new User(3L, "operator", "operatorpass", "operator@msgtel.com", UserRole.OPERATOR, now);

        assertEquals(3L, constructedUser.getUserId());
        assertEquals("operator", constructedUser.getUsername());
        assertEquals("operatorpass", constructedUser.getPasswordHash());
        assertEquals("operator@msgtel.com", constructedUser.getEmail());
        assertEquals(UserRole.OPERATOR, constructedUser.getRole());
        assertEquals(now, constructedUser.getCreatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNull(emptyUser.getUserId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getPasswordHash());
        assertNull(emptyUser.getEmail());
        assertEquals(UserRole.CUSTOMER, emptyUser.getRole()); // Default role
        assertNull(emptyUser.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .userId(1L)
                .username("testuser")
                .passwordHash("password")
                .email("test@msgtel.com")
                .role(UserRole.CUSTOMER)
                .createdAt(testTime)
                .build();

        User user2 = User.builder()
                .userId(1L)
                .username("testuser")
                .passwordHash("password")
                .email("test@msgtel.com")
                .role(UserRole.CUSTOMER)
                .createdAt(testTime)
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEquals() {
        User user1 = User.builder()
                .userId(1L)
                .username("testuser1")
                .build();

        User user2 = User.builder()
                .userId(2L)
                .username("testuser2")
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    void testGetPassword_ReturnsPasswordHash() {
        user.setPasswordHash("securePassword123");
        assertEquals("securePassword123", user.getPassword());
    }

    @Test
    void testGetAuthorities_Customer() {
        user.setRole(UserRole.CUSTOMER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void testGetAuthorities_Admin() {
        user.setRole(UserRole.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetAuthorities_Operator() {
        user.setRole(UserRole.OPERATOR);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testToString() {
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("test@msgtel.com"));
    }

    @Test
    void testDefaultRole() {
        User newUser = new User();
        assertEquals(UserRole.CUSTOMER, newUser.getRole());
    }

    @Test
    void testSetters() {
        User testUser = new User();

        testUser.setUserId(100L);
        assertEquals(100L, testUser.getUserId());

        testUser.setUsername("newuser");
        assertEquals("newuser", testUser.getUsername());

        testUser.setPasswordHash("newpassword");
        assertEquals("newpassword", testUser.getPasswordHash());

        testUser.setEmail("new@msgtel.com");
        assertEquals("new@msgtel.com", testUser.getEmail());

        testUser.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, testUser.getRole());

        LocalDateTime now = LocalDateTime.now();
        testUser.setCreatedAt(now);
        assertEquals(now, testUser.getCreatedAt());
    }

    @Test
    void testUserDetailsImplementation() {
        // Verify User implements UserDetails interface correctly
        assertTrue(user instanceof org.springframework.security.core.userdetails.UserDetails);

        // Test all UserDetails methods
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword123", user.getPassword());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertFalse(user.getAuthorities().isEmpty());
    }

    @Test
    void testBuilderWithNulls() {
        User nullUser = User.builder()
                .userId(null)
                .username(null)
                .passwordHash(null)
                .email(null)
                .role(null)
                .createdAt(null)
                .build();

        assertNull(nullUser.getUserId());
        assertNull(nullUser.getUsername());
        assertNull(nullUser.getPasswordHash());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getRole());
        assertNull(nullUser.getCreatedAt());
    }
}
