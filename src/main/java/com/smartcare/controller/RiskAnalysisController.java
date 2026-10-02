package com.smartcare.controller;

import com.smartcare.model.HealthRecord;
import com.smartcare.model.RiskAnalysis;
import com.smartcare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/risk-analysis")
public class RiskAnalysisController {

    private final HealthRecordRepository healthRecordRepository;

    public RiskAnalysisController(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }

    @GetMapping("/patient/{patientId}")
    public RiskAnalysis analyzePatient(@PathVariable Long patientId) {

        List<HealthRecord> records =
                healthRecordRepository.findByPatientId(patientId);

        if (records.isEmpty()) {
            return new RiskAnalysis(
                    "NO DATA",
                    List.of("No health record found."),
                    "Please add a health record before analysis."
            );
        }

        HealthRecord latest = records.get(records.size() - 1);

        List<String> alerts = new ArrayList<>();

        if (latest.getOxygenLevel() < 90) {
            alerts.add("Oxygen level is critically low.");
        }

        if (latest.getHeartRate() > 120) {
            alerts.add("Heart rate is high.");
        }

        if (latest.getHeartRate() < 50) {
            alerts.add("Heart rate is low.");
        }

        if (latest.getTemperature() >= 100.4) {
            alerts.add("Temperature is elevated.");
        }

        if (alerts.isEmpty()) {
            return new RiskAnalysis(
                    "LOW",
                    List.of("No abnormal vital signs detected."),
                    "Continue regular health monitoring."
            );
        }

        if (latest.getOxygenLevel() < 90) {
            return new RiskAnalysis(
                    "CRITICAL",
                    alerts,
                    "Seek prompt medical attention for critically low oxygen."
            );
        }

        return new RiskAnalysis(
                "MODERATE",
                alerts,
                "Monitor the patient and consider consulting a healthcare professional if symptoms persist or worsen."
        );
    }
}
