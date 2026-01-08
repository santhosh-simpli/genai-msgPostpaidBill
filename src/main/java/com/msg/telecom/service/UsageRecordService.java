package com.msg.telecom.service;

import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsageRecordService {

    private final UsageRecordRepository usageRecordRepository;

    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    public UsageRecord getUsageRecordById(Long id) {
        return usageRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage record not found with id: " + id));
    }

    public List<UsageRecord> getUsageRecordsByServiceId(Long serviceId) {
        return usageRecordRepository.findByService_ServiceId(serviceId);
    }

    public UsageRecord createUsageRecord(UsageRecord usageRecord) {
        return usageRecordRepository.save(usageRecord);
    }

    public UsageRecord updateUsageRecord(Long id, UsageRecord usageRecordDetails) {
        UsageRecord usageRecord = getUsageRecordById(id);
        usageRecord.setUsageDate(usageRecordDetails.getUsageDate());
        usageRecord.setUsageAmount(usageRecordDetails.getUsageAmount());
        usageRecord.setUnit(usageRecordDetails.getUnit());
        return usageRecordRepository.save(usageRecord);
    }

    public void deleteUsageRecord(Long id) {
        usageRecordRepository.deleteById(id);
    }
}
