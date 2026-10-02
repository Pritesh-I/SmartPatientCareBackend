package com.smartcare.controller;

import com.smartcare.model.HealthAlert;
import com.smartcare.model.HealthRecord;
import com.smartcare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-alerts")
public class HealthAlertController {

    private final HealthRecordRepository healthRecordRepository;

    public HealthAlertController(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }

    @GetMapping("/patient/{patientId}")
    public HealthAlert checkHealth(@PathVariable Long patientId) {

        var records = healthRecordRepository.findByPatientId(patientId);

        if (records.isEmpty()) {
            return new HealthAlert(
                    "NO DATA",
                    "No health record found for this patient."
            );
        }

        HealthRecord latest = records.get(records.size() - 1);

        if (latest.getOxygenLevel() < 90) {
            return new HealthAlert(
                    "CRITICAL",
                    "Oxygen level is critically low."
            );
        }

        if (latest.getHeartRate() > 120 || latest.getHeartRate() < 50) {
            return new HealthAlert(
                    "WARNING",
                    "Heart rate is outside the normal range."
            );
        }

        if (latest.getTemperature() >= 100.4) {
            return new HealthAlert(
                    "WARNING",
                    "Temperature is high."
            );
        }

        return new HealthAlert(
                "NORMAL",
                "Patient vital signs are currently within the configured alert ranges."
        );
    }
}
