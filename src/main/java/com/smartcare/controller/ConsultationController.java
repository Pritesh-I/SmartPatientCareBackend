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

        if (consultation.getStatus() == null ||
            consultation.getStatus().isBlank()) {
            consultation.setStatus("PENDING_APPROVAL");
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

    @GetMapping("/doctor/{doctorId}/pending")
    public List<Consultation> pendingDoctorApprovals(
            @PathVariable Long doctorId) {

        return repository
                .findByDoctorIdAndStatusOrderByConsultationDateDesc(
                        doctorId, "PENDING_APPROVAL");
    }

    @PutMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Consultation consultation =
                repository.findById(id).orElse(null);

        if (consultation == null) {
            return java.util.Map.of(
                    "success", false,
                    "message", "Consultation not found."
            );
        }

        String newStatus = status.toUpperCase();

        if (!newStatus.equals("PENDING_APPROVAL") &&
            !newStatus.equals("APPROVED") &&
            !newStatus.equals("REJECTED")) {

            return java.util.Map.of(
                    "success", false,
                    "message",
                    "Invalid status. Use PENDING_APPROVAL, APPROVED or REJECTED."
            );
        }

        consultation.setStatus(newStatus);

        Consultation saved = repository.save(consultation);

        return java.util.Map.of(
                "success", true,
                "message", "Consultation status updated.",
                "consultation", saved
        );
    }
}
