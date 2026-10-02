package com.smartcare.repository;

import com.smartcare.model.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    List<FollowUp> findByPatientId(Long patientId);

    List<FollowUp> findByDoctorId(Long doctorId);

    List<FollowUp> findByConsultationId(Long consultationId);
}
