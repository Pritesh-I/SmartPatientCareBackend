package com.smartcare.controller;

import com.smartcare.model.DoctorAvailability;
import com.smartcare.repository.DoctorAvailabilityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityRepository repository;

    public DoctorAvailabilityController(
            DoctorAvailabilityRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/availability")
    public DoctorAvailability createAvailability(
            @RequestBody DoctorAvailability availability) {

        return repository.save(availability);
    }

    @GetMapping("/{doctorId}/availability")
    public List<DoctorAvailability> getAvailability(
            @PathVariable Long doctorId) {

        return repository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(
                doctorId);
    }

    @PutMapping("/availability/{id}")
    public DoctorAvailability updateAvailability(
            @PathVariable Long id,
            @RequestBody DoctorAvailability updatedAvailability) {

        DoctorAvailability existing =
                repository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setDoctorId(updatedAvailability.getDoctorId());
        existing.setDayOfWeek(updatedAvailability.getDayOfWeek());
        existing.setStartTime(updatedAvailability.getStartTime());
        existing.setEndTime(updatedAvailability.getEndTime());
        existing.setAvailable(updatedAvailability.isAvailable());

        return repository.save(existing);
    }

    @DeleteMapping("/availability/{id}")
    public String deleteAvailability(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return "Availability not found.";
        }

        repository.deleteById(id);

        return "Availability deleted successfully.";
    }
}
