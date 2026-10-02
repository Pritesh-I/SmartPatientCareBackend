package com.smartcare.repository;

import com.smartcare.model.MedicineRefill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRefillRepository extends JpaRepository<MedicineRefill, Long> {

    List<MedicineRefill> findByPatientId(Long patientId);

    List<MedicineRefill> findByDoctorId(Long doctorId);

    List<MedicineRefill> findByPrescriptionId(Long prescriptionId);

    List<MedicineRefill> findByStatus(String status);
}
