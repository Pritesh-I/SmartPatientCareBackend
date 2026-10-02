package com.smartcare.repository;

import com.smartcare.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId);
}
