package com.smartcare.controller;

import com.smartcare.model.Prescription;
import com.smartcare.repository.PrescriptionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

        return repository.save(prescription);
    }

    @GetMapping("/patient/{patientId}")
    public List<Prescription> getPatientPrescriptions(
            @PathVariable Long patientId) {

        return repository.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Prescription> getDoctorPrescriptions(
            @PathVariable Long doctorId) {

        return repository.findByDoctorId(doctorId);
    }

    @GetMapping("/consultation/{consultationId}")
    public List<Prescription> getConsultationPrescriptions(
            @PathVariable Long consultationId) {

        return repository.findByConsultationId(consultationId);
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
