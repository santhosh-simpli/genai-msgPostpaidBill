package com.msg.telecom.service;

import com.msg.telecom.model.Service;
import com.msg.telecom.model.Customer;
import com.msg.telecom.repository.ServiceRepository;
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

@DisplayName("ServiceService Extended Tests")
class ServiceServiceExtendedTest {

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
        testCustomer.setFullName("John Doe");

        testService = Service.builder()
                .serviceId(1L)
                .customer(testCustomer)
                .serviceType("Mobile")
                .startDate(LocalDate.of(2025, 1, 1))
                .status("Active")
                .build();
    }

    @Nested
    @DisplayName("getAllServices Extended Tests")
    class GetAllServicesExtendedTests {

        @Test
        @DisplayName("Should return services with different types")
        void getAllServices_DifferentTypes() {
            Service broadband = Service.builder()
                    .serviceId(2L)
                    .serviceType("Broadband")
                    .status("Active")
                    .build();
            Service cableTV = Service.builder()
                    .serviceId(3L)
                    .serviceType("Cable TV")
                    .status("Active")
                    .build();
            Service voip = Service.builder()
                    .serviceId(4L)
                    .serviceType("VoIP")
                    .status("Suspended")
                    .build();

            when(serviceRepository.findAll())
                    .thenReturn(Arrays.asList(testService, broadband, cableTV, voip));

            List<Service> result = serviceService.getAllServices();

            assertEquals(4, result.size());
            assertTrue(result.stream().anyMatch(s -> "Mobile".equals(s.getServiceType())));
            assertTrue(result.stream().anyMatch(s -> "Broadband".equals(s.getServiceType())));
            assertTrue(result.stream().anyMatch(s -> "Cable TV".equals(s.getServiceType())));
            assertTrue(result.stream().anyMatch(s -> "VoIP".equals(s.getServiceType())));
        }

        @Test
        @DisplayName("Should return services with mixed statuses")
        void getAllServices_MixedStatuses() {
            Service suspendedService = Service.builder()
                    .serviceId(2L)
                    .serviceType("Broadband")
                    .status("Suspended")
                    .build();
            Service terminatedService = Service.builder()
                    .serviceId(3L)
                    .serviceType("Cable TV")
                    .status("Terminated")
                    .build();

            when(serviceRepository.findAll())
                    .thenReturn(Arrays.asList(testService, suspendedService, terminatedService));

            List<Service> result = serviceService.getAllServices();

            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(s -> "Active".equals(s.getStatus())));
            assertTrue(result.stream().anyMatch(s -> "Suspended".equals(s.getStatus())));
            assertTrue(result.stream().anyMatch(s -> "Terminated".equals(s.getStatus())));
        }

        @Test
        @DisplayName("Should return empty list when no services")
        void getAllServices_EmptyList() {
            when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

            List<Service> result = serviceService.getAllServices();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should verify repository called once")
        void getAllServices_RepositoryCalledOnce() {
            when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

            serviceService.getAllServices();

            verify(serviceRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getServiceById Extended Tests")
    class GetServiceByIdExtendedTests {

        @Test
        @DisplayName("Should return service with all fields populated")
        void getServiceById_AllFields() {
            when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));

            Service result = serviceService.getServiceById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getServiceId());
            assertEquals("Mobile", result.getServiceType());
            assertEquals("Active", result.getStatus());
            assertEquals(LocalDate.of(2025, 1, 1), result.getStartDate());
            assertNotNull(result.getCustomer());
        }

        @Test
        @DisplayName("Should throw exception with correct ID in message")
        void getServiceById_NotFound_CorrectId() {
            when(serviceRepository.findById(789L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceService.getServiceById(789L));

            assertTrue(exception.getMessage().contains("Service not found"));
            assertTrue(exception.getMessage().contains("789"));
        }

        @Test
        @DisplayName("Should handle very large ID")
        void getServiceById_LargeId() {
            long largeId = Long.MAX_VALUE;
            when(serviceRepository.findById(largeId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> serviceService.getServiceById(largeId));
        }
    }

    @Nested
    @DisplayName("getServicesByCustomerId Extended Tests")
    class GetServicesByCustomerIdExtendedTests {

        @Test
        @DisplayName("Should return all services for customer")
        void getServicesByCustomerId_MultipleServices() {
            Service service2 = Service.builder()
                    .serviceId(2L)
                    .customer(testCustomer)
                    .serviceType("Broadband")
                    .status("Active")
                    .build();

            when(serviceRepository.findByCustomer_CustomerId(1L))
                    .thenReturn(Arrays.asList(testService, service2));

            List<Service> result = serviceService.getServicesByCustomerId(1L);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list for customer with no services")
        void getServicesByCustomerId_NoServices() {
            when(serviceRepository.findByCustomer_CustomerId(999L))
                    .thenReturn(Collections.emptyList());

            List<Service> result = serviceService.getServicesByCustomerId(999L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should verify correct customer ID passed to repository")
        void getServicesByCustomerId_CorrectId() {
            when(serviceRepository.findByCustomer_CustomerId(42L))
                    .thenReturn(Collections.emptyList());

            serviceService.getServicesByCustomerId(42L);

            verify(serviceRepository).findByCustomer_CustomerId(42L);
        }
    }

    @Nested
    @DisplayName("createService Extended Tests")
    class CreateServiceExtendedTests {

        @Test
        @DisplayName("Should create Mobile service")
        void createService_Mobile() {
            Service newService = Service.builder()
                    .customer(testCustomer)
                    .serviceType("Mobile")
                    .startDate(LocalDate.now())
                    .status("Active")
                    .build();

            when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
                Service saved = invocation.getArgument(0);
                saved.setServiceId(100L);
                return saved;
            });

            Service result = serviceService.createService(newService);

            assertEquals(100L, result.getServiceId());
            assertEquals("Mobile", result.getServiceType());
        }

        @Test
        @DisplayName("Should create Broadband service")
        void createService_Broadband() {
            Service newService = Service.builder()
                    .customer(testCustomer)
                    .serviceType("Broadband")
                    .status("Active")
                    .build();

            when(serviceRepository.save(any(Service.class))).thenReturn(newService);

            Service result = serviceService.createService(newService);

            assertEquals("Broadband", result.getServiceType());
        }

        @Test
        @DisplayName("Should create service with Suspended status")
        void createService_SuspendedStatus() {
            Service newService = Service.builder()
                    .customer(testCustomer)
                    .serviceType("Cable TV")
                    .status("Suspended")
                    .build();

            when(serviceRepository.save(any(Service.class))).thenReturn(newService);

            Service result = serviceService.createService(newService);

            assertEquals("Suspended", result.getStatus());
        }

        @Test
        @DisplayName("Should create VoIP service")
        void createService_VoIP() {
            Service newService = Service.builder()
                    .customer(testCustomer)
                    .serviceType("VoIP")
                    .status("Active")
                    .build();

            when(serviceRepository.save(any(Service.class))).thenReturn(newService);

            Service result = serviceService.createService(newService);

            assertEquals("VoIP", result.getServiceType());
        }
    }

    @Nested
    @DisplayName("updateService Extended Tests")
    class UpdateServiceExtendedTests {

        @Test
        @DisplayName("Should update service type")
        void updateService_ServiceType() {
            Service updateDetails = Service.builder()
                    .serviceType("Broadband")
                    .status(testService.getStatus())
                    .build();

            when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Service result = serviceService.updateService(1L, updateDetails);

            assertEquals("Broadband", result.getServiceType());
        }

        @Test
        @DisplayName("Should update status to Suspended")
        void updateService_StatusSuspended() {
            Service updateDetails = Service.builder()
                    .serviceType(testService.getServiceType())
                    .status("Suspended")
                    .build();

            when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Service result = serviceService.updateService(1L, updateDetails);

            assertEquals("Suspended", result.getStatus());
        }

        @Test
        @DisplayName("Should update status to Terminated")
        void updateService_StatusTerminated() {
            Service updateDetails = Service.builder()
                    .serviceType(testService.getServiceType())
                    .status("Terminated")
                    .build();

            when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Service result = serviceService.updateService(1L, updateDetails);

            assertEquals("Terminated", result.getStatus());
        }

        @Test
        @DisplayName("Should update both type and status")
        void updateService_BothFields() {
            Service updateDetails = Service.builder()
                    .serviceType("VoIP")
                    .status("Active")
                    .build();

            when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Service result = serviceService.updateService(1L, updateDetails);

            assertEquals("VoIP", result.getServiceType());
            assertEquals("Active", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception for non-existent service")
        void updateService_NotFound() {
            Service updateDetails = Service.builder().status("Active").build();
            when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> serviceService.updateService(999L, updateDetails));

            verify(serviceRepository, never()).save(any(Service.class));
        }
    }

    @Nested
    @DisplayName("deleteService Extended Tests")
    class DeleteServiceExtendedTests {

        @Test
        @DisplayName("Should delete existing service")
        void deleteService_Existing() {
            doNothing().when(serviceRepository).deleteById(1L);

            assertDoesNotThrow(() -> serviceService.deleteService(1L));

            verify(serviceRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should not throw for non-existent service")
        void deleteService_NonExistent() {
            doNothing().when(serviceRepository).deleteById(9999L);

            assertDoesNotThrow(() -> serviceService.deleteService(9999L));
        }

        @Test
        @DisplayName("Should call repository with correct ID")
        void deleteService_CorrectId() {
            doNothing().when(serviceRepository).deleteById(123L);

            serviceService.deleteService(123L);

            verify(serviceRepository).deleteById(123L);
        }
    }
}
