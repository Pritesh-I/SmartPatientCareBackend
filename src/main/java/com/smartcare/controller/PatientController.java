package com.smartcare.controller;

import com.smartcare.model.Patient;
import com.smartcare.model.HealthRecord;
import com.smartcare.repository.PatientRepository;
import com.smartcare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;

    public PatientController(PatientRepository patientRepository,
                             HealthRecordRepository healthRecordRepository) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @GetMapping("/{id}/dashboard")
    public Map<String, Object> getPatientDashboard(@PathVariable Long id) {

        Patient patient = patientRepository.findById(id).orElse(null);

        if (patient == null) {
            return Map.of("error", "Patient not found");
        }

        List<HealthRecord> healthRecords =
                healthRecordRepository.findByPatientId(id);

        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("patient", patient);
        dashboard.put("healthRecords", healthRecords);

        return dashboard;
    }
}
