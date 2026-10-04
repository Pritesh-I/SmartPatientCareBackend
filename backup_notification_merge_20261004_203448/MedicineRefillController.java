package com.smartcare.controller;

import com.smartcare.model.MedicineRefill;
import com.smartcare.repository.MedicineRefillRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medicine-refills")
public class MedicineRefillController {

    private final MedicineRefillRepository repository;

    public MedicineRefillController(MedicineRefillRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public MedicineRefill createRefill(@RequestBody MedicineRefill refill) {

        if (refill.getStatus() == null || refill.getStatus().isBlank()) {
            refill.setStatus("PENDING");
        }

        if (refill.getRequestDate() == null) {
            refill.setRequestDate(LocalDate.now());
        }

        return repository.save(refill);
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicineRefill> getPatientRefills(
            @PathVariable Long patientId) {

        return repository.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<MedicineRefill> getDoctorRefills(
            @PathVariable Long doctorId) {

        return repository.findByDoctorId(doctorId);
    }

    @GetMapping("/prescription/{prescriptionId}")
    public List<MedicineRefill> getPrescriptionRefills(
            @PathVariable Long prescriptionId) {

        return repository.findByPrescriptionId(prescriptionId);
    }

    @GetMapping("/status/{status}")
    public List<MedicineRefill> getRefillsByStatus(
            @PathVariable String status) {

        return repository.findByStatus(status.toUpperCase());
    }

    @PutMapping("/{id}/status")
    public MedicineRefill updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        MedicineRefill refill = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refill request not found"));

        refill.setStatus(status.toUpperCase());

        return repository.save(refill);
    }

    @DeleteMapping("/{id}")
    public String deleteRefill(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return "Refill request not found";
        }

        repository.deleteById(id);

        return "Refill request deleted successfully.";
    }
}
