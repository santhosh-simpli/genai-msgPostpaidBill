package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsageRecordRepositoryTest {

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Customer testCustomer;
    private Service testService;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("usageuser");
        testUser.setEmail("usage@example.com");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = entityManager.persistAndFlush(testUser);

        testCustomer = new Customer();
        testCustomer.setFullName("Usage Customer");
        testCustomer.setEmail("usage.customer@example.com");
        testCustomer.setAddress("123 Usage St");
        testCustomer.setPhoneNumber("1112223333");
        testCustomer.setUser(testUser);
        testCustomer = entityManager.persistAndFlush(testCustomer);

        testService = new Service();
        testService.setServiceType("DATA");
        testService.setStatus("ACTIVE");
        testService.setStartDate(LocalDate.now());
        testService.setCustomer(testCustomer);
        testService = entityManager.persistAndFlush(testService);
    }

    @Test
    void testSaveUsageRecord_Success() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setService(testService);
        usageRecord.setUnit("GB");
        usageRecord.setUsageAmount(100.0);
        usageRecord.setUsageDate(LocalDate.now());

        UsageRecord saved = usageRecordRepository.save(usageRecord);

        assertNotNull(saved.getUsageId());
        assertEquals("GB", saved.getUnit());
        assertEquals(100.0, saved.getUsageAmount());
    }

    @Test
    void testFindById_Success() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setService(testService);
        usageRecord.setUnit("MINUTES");
        usageRecord.setUsageAmount(50.0);
        usageRecord.setUsageDate(LocalDate.now());
        usageRecord = usageRecordRepository.save(usageRecord);

        Optional<UsageRecord> found = usageRecordRepository.findById(usageRecord.getUsageId());

        assertTrue(found.isPresent());
        assertEquals("MINUTES", found.get().getUnit());
    }

    @Test
    void testFindById_NotFound() {
        Optional<UsageRecord> found = usageRecordRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByService_ServiceId_ReturnsUsageRecords() {
        UsageRecord record1 = new UsageRecord();
        record1.setService(testService);
        record1.setUnit("GB");
        record1.setUsageAmount(200.0);
        record1.setUsageDate(LocalDate.now());
        usageRecordRepository.save(record1);

        UsageRecord record2 = new UsageRecord();
        record2.setService(testService);
        record2.setUnit("SMS");
        record2.setUsageAmount(10.0);
        record2.setUsageDate(LocalDate.now());
        usageRecordRepository.save(record2);

        List<UsageRecord> records = usageRecordRepository.findByService_ServiceId(testService.getServiceId());

        assertEquals(2, records.size());
    }

    @Test
    void testFindByService_ServiceId_NoRecords_ReturnsEmptyList() {
        List<UsageRecord> records = usageRecordRepository.findByService_ServiceId(999L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testFindAll_ReturnsAllRecords() {
        UsageRecord record1 = new UsageRecord();
        record1.setService(testService);
        record1.setUnit("GB");
        record1.setUsageAmount(100.0);
        record1.setUsageDate(LocalDate.now());
        usageRecordRepository.save(record1);

        UsageRecord record2 = new UsageRecord();
        record2.setService(testService);
        record2.setUnit("MINUTES");
        record2.setUsageAmount(30.0);
        record2.setUsageDate(LocalDate.now());
        usageRecordRepository.save(record2);

        List<UsageRecord> allRecords = usageRecordRepository.findAll();

        assertTrue(allRecords.size() >= 2);
    }

    @Test
    void testDeleteUsageRecord_Success() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setService(testService);
        usageRecord.setUnit("GB");
        usageRecord.setUsageAmount(50.0);
        usageRecord.setUsageDate(LocalDate.now());
        usageRecord = usageRecordRepository.save(usageRecord);

        Long usageId = usageRecord.getUsageId();
        usageRecordRepository.deleteById(usageId);

        Optional<UsageRecord> deleted = usageRecordRepository.findById(usageId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testUpdateUsageRecord_Success() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setService(testService);
        usageRecord.setUnit("GB");
        usageRecord.setUsageAmount(100.0);
        usageRecord.setUsageDate(LocalDate.now());
        usageRecord = usageRecordRepository.save(usageRecord);

        usageRecord.setUsageAmount(200.0);
        usageRecord.setUnit("MINUTES");
        UsageRecord updated = usageRecordRepository.save(usageRecord);

        assertEquals(200.0, updated.getUsageAmount());
        assertEquals("MINUTES", updated.getUnit());
    }

    @Test
    void testCountUsageRecords() {
        UsageRecord record = new UsageRecord();
        record.setService(testService);
        record.setUnit("GB");
        record.setUsageAmount(75.0);
        record.setUsageDate(LocalDate.now());
        usageRecordRepository.save(record);

        long count = usageRecordRepository.count();

        assertTrue(count >= 1);
    }
}
