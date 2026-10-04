package com.smartcare.controller;

import com.smartcare.model.Prescription;
import com.smartcare.repository.PrescriptionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionRepository repository;

    public PrescriptionController(PrescriptionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Prescription createPrescription(
            @RequestBody Prescription prescription) {

        if (prescription.getPrescribedDate() == null) {
            prescription.setPrescribedDate(LocalDate.now());
        }

        if (prescription.getStatus() == null ||
            prescription.getStatus().isBlank()) {
            prescription.setStatus("PENDING_APPROVAL");
        }

        if (prescription.getConsultationId() != null &&
            !repository.findByConsultationId(prescription.getConsultationId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "A prescription already exists for this consultation.");
        }

        return repository.save(prescription);
    }

    @GetMapping("/patient/{patientId}")
    public List<Prescription> getPatientPrescriptions(
            @PathVariable Long patientId) {

        return repository.findByPatientIdAndStatus(
                patientId, "APPROVED");
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Prescription> getDoctorPrescriptions(
            @PathVariable Long doctorId) {

        return repository.findByDoctorId(doctorId);
    }

    @GetMapping("/doctor/{doctorId}/pending")
    public List<Prescription> getPendingPrescriptions(
            @PathVariable Long doctorId) {

        return repository.findByDoctorIdAndStatus(
                doctorId, "PENDING_APPROVAL");
    }

    @GetMapping("/consultation/{consultationId}")
    public List<Prescription> getConsultationPrescriptions(
            @PathVariable Long consultationId) {

        return repository.findByConsultationIdAndStatus(
                consultationId, "APPROVED");
    }

    @PutMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Prescription prescription =
                repository.findById(id).orElse(null);

        if (prescription == null) {
            return Map.of(
                    "success", false,
                    "message", "Prescription not found."
            );
        }

        String newStatus = status.toUpperCase();

        if (!newStatus.equals("PENDING_APPROVAL") &&
            !newStatus.equals("APPROVED") &&
            !newStatus.equals("REJECTED")) {

            return Map.of(
                    "success", false,
                    "message",
                    "Invalid status. Use PENDING_APPROVAL, APPROVED or REJECTED."
            );
        }

        prescription.setStatus(newStatus);

        Prescription saved = repository.save(prescription);

        return Map.of(
                "success", true,
                "message", "Prescription status updated.",
                "prescription", saved
        );
    }

    @DeleteMapping("/{id}")
    public String deletePrescription(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return "Prescription not found.";
        }

        repository.deleteById(id);
        return "Prescription deleted successfully.";
    }
}
