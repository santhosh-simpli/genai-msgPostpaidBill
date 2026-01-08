package com.msg.telecom.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testCustomerRole() {
        UserRole role = UserRole.CUSTOMER;
        assertNotNull(role);
        assertEquals("CUSTOMER", role.name());
    }

    @Test
    void testAdminRole() {
        UserRole role = UserRole.ADMIN;
        assertNotNull(role);
        assertEquals("ADMIN", role.name());
    }

    @Test
    void testOperatorRole() {
        UserRole role = UserRole.OPERATOR;
        assertNotNull(role);
        assertEquals("OPERATOR", role.name());
    }

    @Test
    void testValueOfCustomer() {
        UserRole role = UserRole.valueOf("CUSTOMER");
        assertEquals(UserRole.CUSTOMER, role);
    }

    @Test
    void testValueOfAdmin() {
        UserRole role = UserRole.valueOf("ADMIN");
        assertEquals(UserRole.ADMIN, role);
    }

    @Test
    void testValueOfOperator() {
        UserRole role = UserRole.valueOf("OPERATOR");
        assertEquals(UserRole.OPERATOR, role);
    }

    @Test
    void testValuesContainsAllRoles() {
        UserRole[] roles = UserRole.values();
        assertEquals(3, roles.length);
        
        boolean hasCustomer = false;
        boolean hasAdmin = false;
        boolean hasOperator = false;
        
        for (UserRole role : roles) {
            if (role == UserRole.CUSTOMER) hasCustomer = true;
            if (role == UserRole.ADMIN) hasAdmin = true;
            if (role == UserRole.OPERATOR) hasOperator = true;
        }
        
        assertTrue(hasCustomer);
        assertTrue(hasAdmin);
        assertTrue(hasOperator);
    }

    @Test
    void testOrdinalValues() {
        // Order in enum: CUSTOMER, OPERATOR, ADMIN
        assertEquals(0, UserRole.CUSTOMER.ordinal());
        assertEquals(1, UserRole.OPERATOR.ordinal());
        assertEquals(2, UserRole.ADMIN.ordinal());
    }

    @Test
    void testEnumEquality() {
        assertEquals(UserRole.CUSTOMER, UserRole.CUSTOMER);
        assertEquals(UserRole.ADMIN, UserRole.ADMIN);
        assertEquals(UserRole.OPERATOR, UserRole.OPERATOR);
    }

    @Test
    void testEnumInequality() {
        assertNotEquals(UserRole.CUSTOMER, UserRole.ADMIN);
        assertNotEquals(UserRole.ADMIN, UserRole.OPERATOR);
        assertNotEquals(UserRole.CUSTOMER, UserRole.OPERATOR);
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    void testValueOfCaseSensitive() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("customer");
        });
    }

    @Test
    void testToString() {
        assertEquals("CUSTOMER", UserRole.CUSTOMER.toString());
        assertEquals("ADMIN", UserRole.ADMIN.toString());
        assertEquals("OPERATOR", UserRole.OPERATOR.toString());
    }

    @Test
    void testCompareTo() {
        // Order in enum: CUSTOMER(0), OPERATOR(1), ADMIN(2)
        assertTrue(UserRole.CUSTOMER.compareTo(UserRole.OPERATOR) < 0);
        assertTrue(UserRole.OPERATOR.compareTo(UserRole.ADMIN) < 0);
        assertTrue(UserRole.ADMIN.compareTo(UserRole.CUSTOMER) > 0);
        assertEquals(0, UserRole.CUSTOMER.compareTo(UserRole.CUSTOMER));
    }

    @Test
    void testDeclaringClass() {
        assertEquals(UserRole.class, UserRole.CUSTOMER.getDeclaringClass());
        assertEquals(UserRole.class, UserRole.ADMIN.getDeclaringClass());
        assertEquals(UserRole.class, UserRole.OPERATOR.getDeclaringClass());
    }
}
