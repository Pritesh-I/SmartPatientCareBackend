package com.smartcare.repository;

import com.smartcare.model.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    List<QueueToken> findByQueueDateOrderByTokenNumberAsc(LocalDate queueDate);

    List<QueueToken> findByPatientIdAndQueueDate(Long patientId, LocalDate queueDate);

    Optional<QueueToken> findByQueueDateAndTokenNumber(
            LocalDate queueDate, int tokenNumber);
}
