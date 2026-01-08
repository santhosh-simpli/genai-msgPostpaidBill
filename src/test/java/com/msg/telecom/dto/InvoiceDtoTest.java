package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceDtoTest {

    @Test
    void testInvoiceDtoGettersAndSetters() {
        InvoiceDto dto = new InvoiceDto();
        
        dto.setInvoiceId(1L);
        dto.setCustomerId(10L);
        dto.setTotalAmount(250.50);
        dto.setStatus("PAID");
        dto.setDueDate("2026-02-01");
        
        assertEquals(1L, dto.getInvoiceId());
        assertEquals(10L, dto.getCustomerId());
        assertEquals(250.50, dto.getTotalAmount());
        assertEquals("PAID", dto.getStatus());
        assertEquals("2026-02-01", dto.getDueDate());
    }

    @Test
    void testInvoiceDtoEquality() {
        InvoiceDto dto1 = new InvoiceDto();
        dto1.setInvoiceId(1L);
        dto1.setCustomerId(5L);
        dto1.setTotalAmount(100.0);
        dto1.setStatus("PENDING");
        dto1.setDueDate("2026-01-15");

        InvoiceDto dto2 = new InvoiceDto();
        dto2.setInvoiceId(1L);
        dto2.setCustomerId(5L);
        dto2.setTotalAmount(100.0);
        dto2.setStatus("PENDING");
        dto2.setDueDate("2026-01-15");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testInvoiceDtoInequality() {
        InvoiceDto dto1 = new InvoiceDto();
        dto1.setInvoiceId(1L);
        dto1.setTotalAmount(100.0);

        InvoiceDto dto2 = new InvoiceDto();
        dto2.setInvoiceId(2L);
        dto2.setTotalAmount(200.0);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testInvoiceDtoToString() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(1L);
        dto.setTotalAmount(500.75);
        dto.setStatus("OVERDUE");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("500.75"));
        assertTrue(toString.contains("OVERDUE"));
    }

    @Test
    void testInvoiceDtoNullValues() {
        InvoiceDto dto = new InvoiceDto();
        
        assertNull(dto.getInvoiceId());
        assertNull(dto.getCustomerId());
        assertNull(dto.getTotalAmount());
        assertNull(dto.getStatus());
        assertNull(dto.getDueDate());
    }

    @Test
    void testInvoiceDtoWithZeroAmount() {
        InvoiceDto dto = new InvoiceDto();
        dto.setTotalAmount(0.0);
        
        assertEquals(0.0, dto.getTotalAmount());
    }

    @Test
    void testInvoiceDtoWithNegativeAmount() {
        InvoiceDto dto = new InvoiceDto();
        dto.setTotalAmount(-50.0);
        
        assertEquals(-50.0, dto.getTotalAmount());
    }

    @Test
    void testInvoiceDtoEqualityWithSelf() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(1L);
        assertEquals(dto, dto);
    }

    @Test
    void testInvoiceDtoNotEqualToNull() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(1L);
        assertNotEquals(null, dto);
    }

    @Test
    void testInvoiceDtoNotEqualToDifferentClass() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(1L);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void testInvoiceDtoHashCodeConsistency() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(1L);
        dto.setTotalAmount(100.0);
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testInvoiceDtoInequalityByInvoiceId() {
        InvoiceDto dto1 = new InvoiceDto();
        dto1.setInvoiceId(1L);
        dto1.setCustomerId(5L);

        InvoiceDto dto2 = new InvoiceDto();
        dto2.setInvoiceId(2L);
        dto2.setCustomerId(5L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testInvoiceDtoInequalityByCustomerId() {
        InvoiceDto dto1 = new InvoiceDto();
        dto1.setInvoiceId(1L);
        dto1.setCustomerId(5L);

        InvoiceDto dto2 = new InvoiceDto();
        dto2.setInvoiceId(1L);
        dto2.setCustomerId(10L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testInvoiceDtoInequalityByStatus() {
        InvoiceDto dto1 = new InvoiceDto();
        dto1.setInvoiceId(1L);
        dto1.setStatus("PAID");

        InvoiceDto dto2 = new InvoiceDto();
        dto2.setInvoiceId(1L);
        dto2.setStatus("PENDING");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testInvoiceDtoAllStatuses() {
        String[] statuses = {"PAID", "PENDING", "OVERDUE", "CANCELLED", "PARTIAL"};
        for (String status : statuses) {
            InvoiceDto dto = new InvoiceDto();
            dto.setStatus(status);
            assertEquals(status, dto.getStatus());
        }
    }

    @Test
    void testInvoiceDtoWithLargeAmount() {
        InvoiceDto dto = new InvoiceDto();
        dto.setTotalAmount(999999999.99);
        assertEquals(999999999.99, dto.getTotalAmount());
    }

    @Test
    void testInvoiceDtoWithPreciseAmount() {
        InvoiceDto dto = new InvoiceDto();
        dto.setTotalAmount(123.456789);
        assertEquals(123.456789, dto.getTotalAmount());
    }

    @Test
    void testInvoiceDtoWithMaxLongId() {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(Long.MAX_VALUE);
        dto.setCustomerId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getInvoiceId());
        assertEquals(Long.MAX_VALUE, dto.getCustomerId());
    }
}
