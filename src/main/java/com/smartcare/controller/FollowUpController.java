package com.smartcare.controller;

import com.smartcare.model.FollowUp;
import com.smartcare.repository.FollowUpRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpRepository repository;

    public FollowUpController(FollowUpRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public FollowUp createFollowUp(@RequestBody FollowUp followUp) {
        if (followUp.getStatus() == null || followUp.getStatus().isBlank()) {
            followUp.setStatus("SCHEDULED");
        }

        if (followUp.getFollowUpDate() == null) {
            followUp.setFollowUpDate(LocalDate.now());
        }

        return repository.save(followUp);
    }

    @GetMapping("/patient/{patientId}")
    public List<FollowUp> getPatientFollowUps(@PathVariable Long patientId) {
        return repository.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<FollowUp> getDoctorFollowUps(@PathVariable Long doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    @GetMapping("/consultation/{consultationId}")
    public List<FollowUp> getConsultationFollowUps(
            @PathVariable Long consultationId) {
        return repository.findByConsultationId(consultationId);
    }

    @PutMapping("/{id}/status")
    public FollowUp updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        FollowUp followUp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow-up not found"));

        followUp.setStatus(status.toUpperCase());

        return repository.save(followUp);
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
