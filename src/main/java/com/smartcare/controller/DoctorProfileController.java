package com.smartcare.controller;

import com.smartcare.model.DoctorProfile;
import com.smartcare.repository.DoctorProfileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorProfileController {

    private final DoctorProfileRepository repository;

    public DoctorProfileController(DoctorProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DoctorProfile> getAllDoctors() {
        return repository.findAll();
    }

    @PostMapping("/profile")
    public DoctorProfile createProfile(
            @RequestBody DoctorProfile profile) {

        return repository.save(profile);
    }

    @GetMapping("/{doctorId}/profile")
    public DoctorProfile getProfile(
            @PathVariable Long doctorId) {

        return repository.findByDoctorId(doctorId).orElse(null);
    }

    @PutMapping("/{doctorId}/profile")
    public DoctorProfile updateProfile(
            @PathVariable Long doctorId,
            @RequestBody DoctorProfile updatedProfile) {

        DoctorProfile existing =
                repository.findByDoctorId(doctorId).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setSpecialization(updatedProfile.getSpecialization());
        existing.setQualification(updatedProfile.getQualification());
        existing.setExperienceYears(updatedProfile.getExperienceYears());
        existing.setAbout(updatedProfile.getAbout());
        existing.setConsultationFee(updatedProfile.getConsultationFee());
        existing.setPhone(updatedProfile.getPhone());
        existing.setEmail(updatedProfile.getEmail());

        return repository.save(existing);
    }
}
