package com.msg.telecom.service;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import com.msg.telecom.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceServiceTest {
    @Mock
    private ServiceRepository serviceRepository;
    @InjectMocks
    private ServiceService serviceService;

    private Service testService;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("Test Customer");
        
        testService = new Service();
        testService.setServiceId(1L);
        testService.setCustomer(testCustomer);
        testService.setServiceType("Mobile");
        testService.setStatus("Active");
        testService.setStartDate(LocalDate.now().minusMonths(1));
    }

    @Test
    void getAllServices_ReturnsList() {
        when(serviceRepository.findAll()).thenReturn(List.of(testService));
        List<Service> services = serviceService.getAllServices();
        assertEquals(1, services.size());
        assertEquals("Mobile", services.get(0).getServiceType());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void getAllServices_ReturnsEmptyList() {
        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());
        List<Service> services = serviceService.getAllServices();
        assertTrue(services.isEmpty());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void getServiceById_Found() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        Service result = serviceService.getServiceById(1L);
        assertEquals(1L, result.getServiceId());
        assertEquals("Active", result.getStatus());
        verify(serviceRepository, times(1)).findById(1L);
    }

    @Test
    void getServiceById_NotFound() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> serviceService.getServiceById(999L));
        assertTrue(ex.getMessage().contains("Service not found"));
        verify(serviceRepository, times(1)).findById(999L);
    }

    @Test
    void getServicesByCustomerId_ReturnsList() {
        when(serviceRepository.findByCustomer_CustomerId(1L)).thenReturn(List.of(testService));
        List<Service> services = serviceService.getServicesByCustomerId(1L);
        assertEquals(1, services.size());
        verify(serviceRepository, times(1)).findByCustomer_CustomerId(1L);
    }

    @Test
    void getServicesByCustomerId_ReturnsEmptyList() {
        when(serviceRepository.findByCustomer_CustomerId(999L)).thenReturn(Collections.emptyList());
        List<Service> services = serviceService.getServicesByCustomerId(999L);
        assertTrue(services.isEmpty());
        verify(serviceRepository, times(1)).findByCustomer_CustomerId(999L);
    }

    @Test
    void createService_Success() {
        Service newService = new Service();
        newService.setCustomer(testCustomer);
        newService.setServiceType("Broadband");
        newService.setStatus("Active");
        
        when(serviceRepository.save(any(Service.class))).thenReturn(newService);
        Service created = serviceService.createService(newService);
        
        assertNotNull(created);
        assertEquals("Broadband", created.getServiceType());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void updateService_Success() {
        Service updateDetails = new Service();
        updateDetails.setServiceType("VoIP");
        updateDetails.setStatus("Suspended");
        
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);
        
        Service updated = serviceService.updateService(1L, updateDetails);
        assertNotNull(updated);
        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void updateService_NotFound() {
        Service updateDetails = new Service();
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> serviceService.updateService(999L, updateDetails));
        verify(serviceRepository, times(1)).findById(999L);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void deleteService_Success() {
        doNothing().when(serviceRepository).deleteById(1L);
        
        assertDoesNotThrow(() -> serviceService.deleteService(1L));
        verify(serviceRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteService_NonExistent() {
        doNothing().when(serviceRepository).deleteById(999L);
        
        assertDoesNotThrow(() -> serviceService.deleteService(999L));
        verify(serviceRepository, times(1)).deleteById(999L);
    }
}
