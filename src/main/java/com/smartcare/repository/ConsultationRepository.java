package com.smartcare.repository;

import com.smartcare.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByPatientIdOrderByConsultationDateDesc(Long patientId);

    List<Consultation> findByPatientIdAndStatus(Long patientId, String status);

    List<Consultation> findByDoctorIdOrderByConsultationDateDesc(Long doctorId);

    List<Consultation> findByDoctorIdAndStatusOrderByConsultationDateDesc(
            Long doctorId,
            String status
    );
}
