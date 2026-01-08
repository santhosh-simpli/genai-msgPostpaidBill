package com.msg.telecom.controller;

import com.msg.telecom.dto.UsageRecordDto;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.ServiceService;
import com.msg.telecom.service.UsageRecordService;
import com.msg.telecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceControllerTest {
    @Mock
    private ServiceService serviceService;
    @Mock
    private UsageRecordService usageRecordService;
    @Mock
    private UserService userService;
    @Mock
    private CustomerService customerService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private ServiceController serviceController;

    private User adminUser;
    private User customerUser;
    private User operatorUser;
    private Customer testCustomer;
    private Service testService;
    private UsageRecord testUsageRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        adminUser = new User();
        adminUser.setUserId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(UserRole.ADMIN);
        
        customerUser = new User();
        customerUser.setUserId(2L);
        customerUser.setUsername("customer");
        customerUser.setRole(UserRole.CUSTOMER);
        
        operatorUser = new User();
        operatorUser.setUserId(3L);
        operatorUser.setUsername("operator");
        operatorUser.setRole(UserRole.OPERATOR);
        
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setUser(customerUser);
        
        testService = new Service();
        testService.setServiceId(1L);
        testService.setServiceType("DATA");
        testService.setCustomer(testCustomer);
        
        testUsageRecord = new UsageRecord();
        testUsageRecord.setUsageId(1L);
        testUsageRecord.setUsageAmount(10.0);
        testUsageRecord.setUnit("GB");
        testUsageRecord.setUsageDate(LocalDate.now());
        testUsageRecord.setService(testService);
    }

    @Test
    void getAllServiceUsage_AdminRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(10.0, response.getBody().get(0).getAmount());
        verify(usageRecordService, times(1)).getAllUsageRecords();
    }

    @Test
    void getAllServiceUsage_AdminRole_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllServiceUsage_OperatorRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("operator");
        when(userService.getUserByUsername("operator")).thenReturn(operatorUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(usageRecordService, times(1)).getAllUsageRecords();
    }

    @Test
    void getAllServiceUsage_CustomerRole_ReturnsOwnUsage() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(serviceService.getServicesByCustomerId(1L)).thenReturn(Collections.singletonList(testService));
        when(usageRecordService.getUsageRecordsByServiceId(1L)).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(usageRecordService, never()).getAllUsageRecords();
    }

    @Test
    void getAllServiceUsage_CustomerWithNoCustomerRecord_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllServiceUsage_CustomerWithNoServices_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(serviceService.getServicesByCustomerId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllServiceUsage_MultipleUsageRecords_ReturnsAll() {
        UsageRecord usage2 = new UsageRecord();
        usage2.setUsageId(2L);
        usage2.setUsageAmount(20.0);
        usage2.setUnit("MB");
        usage2.setUsageDate(LocalDate.now());
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Arrays.asList(testUsageRecord, usage2));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllServiceUsage_CustomerWithMultipleServices_ReturnsAllUsage() {
        Service service2 = new Service();
        service2.setServiceId(2L);
        service2.setServiceType("VOICE");
        
        UsageRecord usage2 = new UsageRecord();
        usage2.setUsageId(2L);
        usage2.setUsageAmount(100.0);
        usage2.setUnit("Minutes");
        usage2.setUsageDate(LocalDate.now());
        
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(serviceService.getServicesByCustomerId(1L)).thenReturn(Arrays.asList(testService, service2));
        when(usageRecordService.getUsageRecordsByServiceId(1L)).thenReturn(Collections.singletonList(testUsageRecord));
        when(usageRecordService.getUsageRecordsByServiceId(2L)).thenReturn(Collections.singletonList(usage2));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getServiceUsage_ReturnsUsageDtoList() {
        when(usageRecordService.getUsageRecordsByServiceId(1L)).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getServiceUsage(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(10.0, response.getBody().get(0).getAmount());
        assertEquals("GB", response.getBody().get(0).getUsageType());
    }

    @Test
    void getServiceUsage_NoRecords_ReturnsEmptyList() {
        when(usageRecordService.getUsageRecordsByServiceId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getServiceUsage(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getServiceUsage_MultipleRecords_ReturnsAll() {
        UsageRecord usage2 = new UsageRecord();
        usage2.setUsageId(2L);
        usage2.setUsageAmount(5.0);
        usage2.setUnit("GB");
        usage2.setUsageDate(LocalDate.now().minusDays(1));
        
        when(usageRecordService.getUsageRecordsByServiceId(1L)).thenReturn(Arrays.asList(testUsageRecord, usage2));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getServiceUsage(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void addServiceUsage_ReturnsCreatedDto() {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setAmount(30.0);
        dto.setUsageType("SMS");
        
        UsageRecord createdUsage = new UsageRecord();
        createdUsage.setUsageId(3L);
        createdUsage.setUsageAmount(30.0);
        createdUsage.setUnit("SMS");
        createdUsage.setService(testService);
        
        when(serviceService.getServiceById(1L)).thenReturn(testService);
        when(usageRecordService.createUsageRecord(any(UsageRecord.class))).thenReturn(createdUsage);

        ResponseEntity<UsageRecordDto> response = serviceController.addServiceUsage(1L, dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(30.0, response.getBody().getAmount());
        assertEquals("SMS", response.getBody().getUsageType());
    }

    @Test
    void addServiceUsage_WithAllFields_ReturnsCreatedDto() {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setUsageId(5L);
        dto.setAmount(50.0);
        dto.setUsageType("Minutes");
        dto.setServiceId(1L);
        
        UsageRecord createdUsage = new UsageRecord();
        createdUsage.setUsageId(5L);
        createdUsage.setUsageAmount(50.0);
        createdUsage.setUnit("Minutes");
        createdUsage.setUsageDate(LocalDate.now());
        createdUsage.setService(testService);
        
        when(serviceService.getServiceById(1L)).thenReturn(testService);
        when(usageRecordService.createUsageRecord(any(UsageRecord.class))).thenReturn(createdUsage);

        ResponseEntity<UsageRecordDto> response = serviceController.addServiceUsage(1L, dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void addServiceUsage_SetsServiceOnUsageRecord() {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setAmount(10.0);
        dto.setUsageType("GB");
        
        when(serviceService.getServiceById(1L)).thenReturn(testService);
        when(usageRecordService.createUsageRecord(any(UsageRecord.class))).thenAnswer(invocation -> {
            UsageRecord arg = invocation.getArgument(0);
            assertEquals(testService, arg.getService());
            arg.setUsageId(10L);
            return arg;
        });

        ResponseEntity<UsageRecordDto> response = serviceController.addServiceUsage(1L, dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(serviceService, times(1)).getServiceById(1L);
    }

    @Test
    void getAllServiceUsage_UsageWithNullService_HandlesProperly() {
        testUsageRecord.setService(null);
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getServiceId());
    }

    @Test
    void getAllServiceUsage_UsageWithNullUsageDate_HandlesProperly() {
        testUsageRecord.setUsageDate(null);
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(usageRecordService.getAllUsageRecords()).thenReturn(Collections.singletonList(testUsageRecord));

        ResponseEntity<List<UsageRecordDto>> response = serviceController.getAllServiceUsage(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getUsageDate());
    }
}
