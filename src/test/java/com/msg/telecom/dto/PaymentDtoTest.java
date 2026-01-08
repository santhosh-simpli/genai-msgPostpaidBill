package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDtoTest {

    @Test
    void testPaymentDtoGettersAndSetters() {
        PaymentDto dto = new PaymentDto();
        
        dto.setPaymentId(1L);
        dto.setInvoiceId(10L);
        dto.setAmount(150.00);
        dto.setPaymentDate("2026-01-04");
        dto.setStatus("COMPLETED");
        
        assertEquals(1L, dto.getPaymentId());
        assertEquals(10L, dto.getInvoiceId());
        assertEquals(150.00, dto.getAmount());
        assertEquals("2026-01-04", dto.getPaymentDate());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    void testPaymentDtoEquality() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setInvoiceId(5L);
        dto1.setAmount(200.0);
        dto1.setPaymentDate("2026-01-01");
        dto1.setStatus("PENDING");

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(1L);
        dto2.setInvoiceId(5L);
        dto2.setAmount(200.0);
        dto2.setPaymentDate("2026-01-01");
        dto2.setStatus("PENDING");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testPaymentDtoInequality() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setAmount(100.0);

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(2L);
        dto2.setAmount(300.0);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testPaymentDtoToString() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        dto.setAmount(999.99);
        dto.setStatus("REFUNDED");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("999.99"));
        assertTrue(toString.contains("REFUNDED"));
    }

    @Test
    void testPaymentDtoNullValues() {
        PaymentDto dto = new PaymentDto();
        
        assertNull(dto.getPaymentId());
        assertNull(dto.getInvoiceId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentDate());
        assertNull(dto.getStatus());
    }

    @Test
    void testPaymentDtoWithPartialPayment() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        dto.setAmount(50.0);
        dto.setStatus("PARTIAL");
        
        assertEquals(50.0, dto.getAmount());
        assertEquals("PARTIAL", dto.getStatus());
    }

    @Test
    void testPaymentDtoEqualityWithSelf() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        dto.setAmount(100.0);
        assertEquals(dto, dto);
    }

    @Test
    void testPaymentDtoNotEqualToNull() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        assertNotEquals(null, dto);
    }

    @Test
    void testPaymentDtoNotEqualToDifferentClass() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void testPaymentDtoHashCodeConsistency() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(1L);
        dto.setAmount(100.0);
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testPaymentDtoInequalityByPaymentId() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setInvoiceId(10L);

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(2L);
        dto2.setInvoiceId(10L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testPaymentDtoInequalityByInvoiceId() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setInvoiceId(10L);

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(1L);
        dto2.setInvoiceId(20L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testPaymentDtoInequalityByAmount() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setAmount(100.0);

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(1L);
        dto2.setAmount(200.0);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testPaymentDtoInequalityByStatus() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setPaymentId(1L);
        dto1.setStatus("COMPLETED");

        PaymentDto dto2 = new PaymentDto();
        dto2.setPaymentId(1L);
        dto2.setStatus("PENDING");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testPaymentDtoAllStatuses() {
        String[] statuses = {"COMPLETED", "PENDING", "FAILED", "REFUNDED", "PARTIAL"};
        for (String status : statuses) {
            PaymentDto dto = new PaymentDto();
            dto.setStatus(status);
            assertEquals(status, dto.getStatus());
        }
    }

    @Test
    void testPaymentDtoWithZeroAmount() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(0.0);
        assertEquals(0.0, dto.getAmount());
    }

    @Test
    void testPaymentDtoWithNegativeAmount() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(-100.0);
        assertEquals(-100.0, dto.getAmount());
    }

    @Test
    void testPaymentDtoWithLargeAmount() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(999999999.99);
        assertEquals(999999999.99, dto.getAmount());
    }

    @Test
    void testPaymentDtoWithMaxLongId() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(Long.MAX_VALUE);
        dto.setInvoiceId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getPaymentId());
        assertEquals(Long.MAX_VALUE, dto.getInvoiceId());
    }
}
