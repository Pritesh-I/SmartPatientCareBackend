package com.smartcare.repository;

import com.smartcare.model.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    // Existing global queue
    List<QueueToken> findByQueueDateOrderByTokenNumberAsc(
            LocalDate queueDate);

    // Existing patient queue
    List<QueueToken> findByPatientIdAndQueueDate(
            Long patientId,
            LocalDate queueDate);

    // Existing lookup
    Optional<QueueToken> findByQueueDateAndTokenNumber(
            LocalDate queueDate,
            int tokenNumber);

    // ---------------------------------------------------------
    // Doctor-specific queue methods
    // ---------------------------------------------------------

    // All tokens for one doctor on a specific date
    List<QueueToken> findByDoctorIdAndQueueDateOrderByTokenNumberAsc(
            Long doctorId,
            LocalDate queueDate);

    // Find the latest token number for one doctor on a date
    Optional<QueueToken> findTopByDoctorIdAndQueueDateOrderByTokenNumberDesc(
            Long doctorId,
            LocalDate queueDate);

    // Patient's tokens for one doctor on a specific date
    List<QueueToken> findByPatientIdAndDoctorIdAndQueueDate(
            Long patientId,
            Long doctorId,
            LocalDate queueDate);
}
