package com.smartcare.controller;

import com.smartcare.model.Consultation;
import com.smartcare.repository.ConsultationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationRepository repository;

    public ConsultationController(ConsultationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Consultation create(@RequestBody Consultation consultation) {

        if (consultation.getConsultationDate() == null) {
            consultation.setConsultationDate(LocalDateTime.now());
        }

        return repository.save(consultation);
    }

    @GetMapping("/patient/{patientId}")
    public List<Consultation> patientHistory(
            @PathVariable Long patientId) {

        return repository.findByPatientIdOrderByConsultationDateDesc(
                patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Consultation> doctorHistory(
            @PathVariable Long doctorId) {

        return repository.findByDoctorIdOrderByConsultationDateDesc(
                doctorId);
    }
}
