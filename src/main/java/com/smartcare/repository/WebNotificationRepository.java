package com.smartcare.repository;

import com.smartcare.model.WebNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebNotificationRepository extends JpaRepository<WebNotification,Long> {
    List<WebNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadStatusFalse(Long userId);
}
