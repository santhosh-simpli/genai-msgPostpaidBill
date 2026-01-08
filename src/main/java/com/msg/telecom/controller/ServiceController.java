package com.msg.telecom.controller;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.model.User;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.ServiceService;
import com.msg.telecom.service.UsageRecordService;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.ServiceDto;
import com.msg.telecom.dto.UsageRecordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;
    private final UsageRecordService usageRecordService;
    private final UserService userService;
    private final CustomerService customerService;

    @GetMapping("/service-usage")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<UsageRecordDto>> getAllServiceUsage(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<UsageRecord> allUsage = new ArrayList<>();
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            allUsage = usageRecordService.getAllUsageRecords();
        } else {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (!customers.isEmpty()) {
                List<Service> customerServices = serviceService.getServicesByCustomerId(customers.get(0).getCustomerId());
                for (Service service : customerServices) {
                    allUsage.addAll(usageRecordService.getUsageRecordsByServiceId(service.getServiceId()));
                }
            }
        }
        List<UsageRecordDto> dtos = allUsage.stream().map(this::toUsageDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/services/{id}/usage")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<UsageRecordDto>> getServiceUsage(@PathVariable Long id) {
        List<UsageRecord> usageRecords = usageRecordService.getUsageRecordsByServiceId(id);
        List<UsageRecordDto> dtos = usageRecords.stream().map(this::toUsageDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/services/{id}/usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsageRecordDto> addServiceUsage(@PathVariable Long id, @RequestBody UsageRecordDto usageDto) {
        Service service = serviceService.getServiceById(id);
        UsageRecord usageRecord = toUsageEntity(usageDto);
        usageRecord.setService(service);
        UsageRecord created = usageRecordService.createUsageRecord(usageRecord);
        return ResponseEntity.ok(toUsageDto(created));
    }

    private UsageRecordDto toUsageDto(UsageRecord usage) {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setUsageId(usage.getUsageId());
        dto.setServiceId(usage.getService() != null ? usage.getService().getServiceId() : null);
        dto.setUsageType(usage.getUnit());
        dto.setAmount(usage.getUsageAmount());
        dto.setUsageDate(usage.getUsageDate() != null ? usage.getUsageDate().toString() : null);
        return dto;
    }

    private UsageRecord toUsageEntity(UsageRecordDto dto) {
        UsageRecord usage = new UsageRecord();
        usage.setUsageId(dto.getUsageId());
        usage.setUsageAmount(dto.getAmount());
        usage.setUnit(dto.getUsageType());
        // Set usageDate and service as needed
        return usage;
    }
}
