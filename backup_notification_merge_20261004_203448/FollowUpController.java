package com.smartcare.controller;

import com.smartcare.model.FollowUp;
import com.smartcare.repository.FollowUpRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpRepository repository;

    public FollowUpController(FollowUpRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public FollowUp createFollowUp(@RequestBody FollowUp followUp) {

        if (followUp.getStatus() == null ||
            followUp.getStatus().isBlank()) {

            followUp.setStatus("PENDING_APPROVAL");
        }

        if (followUp.getFollowUpDate() == null) {
            followUp.setFollowUpDate(LocalDate.now());
        }

        return repository.save(followUp);
    }

    @GetMapping("/patient/{patientId}")
    public List<FollowUp> getPatientFollowUps(
            @PathVariable Long patientId) {

        return repository.findByPatientIdAndStatus(
                patientId, "APPROVED");
    }

    @GetMapping("/doctor/{doctorId}")
    public List<FollowUp> getDoctorFollowUps(
            @PathVariable Long doctorId) {

        return repository.findByDoctorId(doctorId);
    }

    @GetMapping("/doctor/{doctorId}/pending")
    public List<FollowUp> getPendingFollowUps(
            @PathVariable Long doctorId) {

        return repository.findByDoctorIdAndStatus(
                doctorId, "PENDING_APPROVAL");
    }

    @GetMapping("/consultation/{consultationId}")
    public List<FollowUp> getConsultationFollowUps(
            @PathVariable Long consultationId) {

        return repository.findByConsultationIdAndStatus(
                consultationId, "APPROVED");
    }

    @PutMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        FollowUp followUp =
                repository.findById(id).orElse(null);

        if (followUp == null) {
            return Map.of(
                    "success", false,
                    "message", "Follow-up not found."
            );
        }

        String newStatus = status.toUpperCase();

        if (!newStatus.equals("PENDING_APPROVAL") &&
            !newStatus.equals("APPROVED") &&
            !newStatus.equals("REJECTED") &&
            !newStatus.equals("SCHEDULED") &&
            !newStatus.equals("COMPLETED")) {

            return Map.of(
                    "success", false,
                    "message", "Invalid follow-up status."
            );
        }

        followUp.setStatus(newStatus);

        FollowUp saved = repository.save(followUp);

        return Map.of(
                "success", true,
                "message", "Follow-up status updated.",
                "followUp", saved
        );
    }

    @DeleteMapping("/{id}")
    public String deleteFollowUp(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return "Follow-up not found";
        }

        repository.deleteById(id);

        return "Follow-up deleted successfully.";
    }
}
