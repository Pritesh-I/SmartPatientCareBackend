package com.smartcare.repository;

import com.smartcare.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByPatientIdAndStatus(
            Long patientId, String status);

    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByDoctorIdAndStatus(
            Long doctorId, String status);

    List<Prescription> findByConsultationId(Long consultationId);

    List<Prescription> findByConsultationIdAndStatus(
            Long consultationId, String status);
}
