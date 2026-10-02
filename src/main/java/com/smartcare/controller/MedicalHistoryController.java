package com.smartcare.controller;

import com.smartcare.model.HealthRecord;
import com.smartcare.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {

    private final HealthRecordRepository repository;

    public MedicalHistoryController(HealthRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/patient/{patientId}")
    public List<HealthRecord> getHistory(@PathVariable Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .sorted((a, b) ->
                        b.getRecordDate().compareTo(a.getRecordDate()))
                .collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}/date/{date}")
    public List<HealthRecord> getByDate(
            @PathVariable Long patientId,
            @PathVariable String date) {

        return repository.findByPatientId(patientId)
                .stream()
                .filter(r -> date.equals(r.getRecordDate()))
                .collect(Collectors.toList());
    }
}
