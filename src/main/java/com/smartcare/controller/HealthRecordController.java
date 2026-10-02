package com.smartcare.controller;

import com.smartcare.model.HealthRecord;
import com.smartcare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-records")
public class HealthRecordController {

    private final HealthRecordRepository healthRecordRepository;

    public HealthRecordController(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }

    @GetMapping
    public List<HealthRecord> getAllRecords() {
        return healthRecordRepository.findAll();
    }

    @GetMapping("/{id}")
    public HealthRecord getRecordById(@PathVariable Long id) {
        return healthRecordRepository.findById(id).orElse(null);
    }

    @PostMapping
    public HealthRecord addRecord(@RequestBody HealthRecord record) {
        return healthRecordRepository.save(record);
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id) {
        if (healthRecordRepository.existsById(id)) {
            healthRecordRepository.deleteById(id);
            return "Health record deleted successfully";
        }
        return "Health record not found";
    }
@GetMapping("/patient/{patientId}")
public List<HealthRecord> getRecordsByPatient(@PathVariable Long patientId) {
    return healthRecordRepository.findByPatientId(patientId);
}
}
