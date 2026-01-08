package com.msg.telecom.service;

import com.msg.telecom.model.Service;
import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.repository.UsageRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UsageRecordService Tests")
class UsageRecordServiceTest {

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private UsageRecordService usageRecordService;

    private UsageRecord testUsageRecord;
    private Service testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testService = new Service();
        testService.setServiceId(1L);
        testService.setServiceType("Mobile");
        testService.setStatus("Active");

        testUsageRecord = new UsageRecord();
        testUsageRecord.setUsageId(1L);
        testUsageRecord.setService(testService);
        testUsageRecord.setUsageDate(LocalDate.now());
        testUsageRecord.setUsageAmount(100.0);
        testUsageRecord.setUnit("MB");
    }

    @Nested
    @DisplayName("getAllUsageRecords Tests")
    class GetAllUsageRecordsTests {

        @Test
        @DisplayName("Should return list of usage records when records exist")
        void getAllUsageRecords_ReturnsList() {
            UsageRecord record2 = new UsageRecord();
            record2.setUsageId(2L);
            record2.setUsageAmount(200.0);

            when(usageRecordRepository.findAll()).thenReturn(Arrays.asList(testUsageRecord, record2));

            List<UsageRecord> result = usageRecordService.getAllUsageRecords();

            assertEquals(2, result.size());
            assertEquals(100.0, result.get(0).getUsageAmount());
            assertEquals(200.0, result.get(1).getUsageAmount());
            verify(usageRecordRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no records exist")
        void getAllUsageRecords_ReturnsEmptyList() {
            when(usageRecordRepository.findAll()).thenReturn(Collections.emptyList());

            List<UsageRecord> result = usageRecordService.getAllUsageRecords();

            assertTrue(result.isEmpty());
            verify(usageRecordRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getUsageRecordById Tests")
    class GetUsageRecordByIdTests {

        @Test
        @DisplayName("Should return usage record when found")
        void getUsageRecordById_Found() {
            when(usageRecordRepository.findById(1L)).thenReturn(Optional.of(testUsageRecord));

            UsageRecord result = usageRecordService.getUsageRecordById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getUsageId());
            assertEquals(100.0, result.getUsageAmount());
            assertEquals("MB", result.getUnit());
            verify(usageRecordRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when usage record not found")
        void getUsageRecordById_NotFound() {
            when(usageRecordRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> usageRecordService.getUsageRecordById(999L));

            assertTrue(exception.getMessage().contains("Usage record not found"));
            verify(usageRecordRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should throw exception with correct ID in message")
        void getUsageRecordById_NotFound_CorrectId() {
            when(usageRecordRepository.findById(42L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> usageRecordService.getUsageRecordById(42L));

            assertTrue(exception.getMessage().contains("42"));
        }
    }

    @Nested
    @DisplayName("getUsageRecordsByServiceId Tests")
    class GetUsageRecordsByServiceIdTests {

        @Test
        @DisplayName("Should return list of records for service")
        void getUsageRecordsByServiceId_ReturnsList() {
            UsageRecord record2 = new UsageRecord();
            record2.setUsageId(2L);
            record2.setService(testService);

            when(usageRecordRepository.findByService_ServiceId(1L))
                    .thenReturn(Arrays.asList(testUsageRecord, record2));

            List<UsageRecord> result = usageRecordService.getUsageRecordsByServiceId(1L);

            assertEquals(2, result.size());
            verify(usageRecordRepository, times(1)).findByService_ServiceId(1L);
        }

        @Test
        @DisplayName("Should return empty list when no records for service")
        void getUsageRecordsByServiceId_ReturnsEmptyList() {
            when(usageRecordRepository.findByService_ServiceId(999L))
                    .thenReturn(Collections.emptyList());

            List<UsageRecord> result = usageRecordService.getUsageRecordsByServiceId(999L);

            assertTrue(result.isEmpty());
            verify(usageRecordRepository, times(1)).findByService_ServiceId(999L);
        }
    }

    @Nested
    @DisplayName("createUsageRecord Tests")
    class CreateUsageRecordTests {

        @Test
        @DisplayName("Should create usage record successfully")
        void createUsageRecord_Success() {
            UsageRecord newRecord = new UsageRecord();
            newRecord.setUsageAmount(500.0);
            newRecord.setUnit("GB");
            newRecord.setUsageDate(LocalDate.now());
            newRecord.setService(testService);

            when(usageRecordRepository.save(any(UsageRecord.class))).thenReturn(newRecord);

            UsageRecord result = usageRecordService.createUsageRecord(newRecord);

            assertNotNull(result);
            assertEquals(500.0, result.getUsageAmount());
            assertEquals("GB", result.getUnit());
            verify(usageRecordRepository, times(1)).save(newRecord);
        }

        @Test
        @DisplayName("Should create usage record with all fields")
        void createUsageRecord_AllFields() {
            UsageRecord newRecord = new UsageRecord();
            newRecord.setUsageAmount(1024.0);
            newRecord.setUnit("Minutes");
            newRecord.setUsageDate(LocalDate.of(2026, 1, 1));
            newRecord.setService(testService);

            when(usageRecordRepository.save(any(UsageRecord.class))).thenAnswer(invocation -> {
                UsageRecord saved = invocation.getArgument(0);
                saved.setUsageId(10L);
                return saved;
            });

            UsageRecord result = usageRecordService.createUsageRecord(newRecord);

            assertNotNull(result);
            assertEquals(10L, result.getUsageId());
            assertEquals(1024.0, result.getUsageAmount());
            assertEquals("Minutes", result.getUnit());
            assertEquals(LocalDate.of(2026, 1, 1), result.getUsageDate());
        }
    }

    @Nested
    @DisplayName("updateUsageRecord Tests")
    class UpdateUsageRecordTests {

        @Test
        @DisplayName("Should update usage record successfully")
        void updateUsageRecord_Success() {
            UsageRecord updateDetails = new UsageRecord();
            updateDetails.setUsageDate(LocalDate.of(2026, 2, 1));
            updateDetails.setUsageAmount(250.0);
            updateDetails.setUnit("GB");

            when(usageRecordRepository.findById(1L)).thenReturn(Optional.of(testUsageRecord));
            when(usageRecordRepository.save(any(UsageRecord.class))).thenReturn(testUsageRecord);

            UsageRecord result = usageRecordService.updateUsageRecord(1L, updateDetails);

            assertNotNull(result);
            verify(usageRecordRepository, times(1)).findById(1L);
            verify(usageRecordRepository, times(1)).save(any(UsageRecord.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent record")
        void updateUsageRecord_NotFound() {
            UsageRecord updateDetails = new UsageRecord();
            updateDetails.setUsageAmount(300.0);

            when(usageRecordRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> usageRecordService.updateUsageRecord(999L, updateDetails));

            verify(usageRecordRepository, never()).save(any(UsageRecord.class));
        }

        @Test
        @DisplayName("Should update all fields correctly")
        void updateUsageRecord_AllFieldsUpdated() {
            UsageRecord updateDetails = new UsageRecord();
            LocalDate newDate = LocalDate.of(2026, 3, 15);
            updateDetails.setUsageDate(newDate);
            updateDetails.setUsageAmount(999.99);
            updateDetails.setUnit("SMS");

            when(usageRecordRepository.findById(1L)).thenReturn(Optional.of(testUsageRecord));
            when(usageRecordRepository.save(any(UsageRecord.class))).thenAnswer(invocation -> {
                UsageRecord saved = invocation.getArgument(0);
                return saved;
            });

            UsageRecord result = usageRecordService.updateUsageRecord(1L, updateDetails);

            assertEquals(newDate, result.getUsageDate());
            assertEquals(999.99, result.getUsageAmount());
            assertEquals("SMS", result.getUnit());
        }
    }

    @Nested
    @DisplayName("deleteUsageRecord Tests")
    class DeleteUsageRecordTests {

        @Test
        @DisplayName("Should delete usage record successfully")
        void deleteUsageRecord_Success() {
            doNothing().when(usageRecordRepository).deleteById(1L);

            assertDoesNotThrow(() -> usageRecordService.deleteUsageRecord(1L));

            verify(usageRecordRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should not throw when deleting non-existent record")
        void deleteUsageRecord_NonExistent() {
            doNothing().when(usageRecordRepository).deleteById(999L);

            assertDoesNotThrow(() -> usageRecordService.deleteUsageRecord(999L));

            verify(usageRecordRepository, times(1)).deleteById(999L);
        }
    }
}
