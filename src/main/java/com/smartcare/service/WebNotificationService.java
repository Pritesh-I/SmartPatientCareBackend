package com.smartcare.service;

import com.smartcare.model.WebNotification;
import com.smartcare.model.UserAccount;
import com.smartcare.repository.UserAccountRepository;
import com.smartcare.repository.WebNotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class WebNotificationService {

    private final WebNotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    public WebNotificationService(
            WebNotificationRepository notificationRepository,
            UserAccountRepository userAccountRepository) {
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public void notifyUser(
            Long userId,
            String title,
            String message) {

        if (userId == null) return;

        notificationRepository.save(
                new WebNotification(userId, title, message)
        );
    }

    public void notifyPatient(
            Long patientId,
            String title,
            String message) {

        if (patientId == null) return;

        userAccountRepository.findAll()
                .stream()
                .filter(u ->
                        "PATIENT".equalsIgnoreCase(u.getRole())
                        && patientId.equals(u.getProfileId()))
                .forEach(u ->
                        notifyUser(
                                u.getId(),
                                title,
                                message
                        )
                );
    }

    public void notifyRole(
            String role,
            String title,
            String message) {

        userAccountRepository.findAll()
                .stream()
                .filter(u ->
                        role.equalsIgnoreCase(u.getRole()))
                .forEach(u ->
                        notifyUser(
                                u.getId(),
                                title,
                                message
                        )
                );
    }
}
