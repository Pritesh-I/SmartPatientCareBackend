package com.smartcare.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.smartcare.model.DeviceToken;
import com.smartcare.repository.DeviceTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FcmService {

    private final DeviceTokenRepository deviceTokenRepository;

    public FcmService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public String sendNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .setToken(token)
                    .build();

            return FirebaseMessaging.getInstance().send(message);

        } catch (Exception e) {
            return "FCM_ERROR: " + e.getMessage();
        }
    }

    public int sendToUser(
            Long userId,
            String title,
            String body) {

        List<DeviceToken> devices =
                deviceTokenRepository.findByUserId(userId);

        int sent = 0;

        for (DeviceToken device : devices) {

            if (device.getToken() == null ||
                    device.getToken().isBlank()) {
                continue;
            }

            String result =
                    sendNotification(
                            device.getToken(),
                            title,
                            body
                    );

            if (!result.startsWith("FCM_ERROR")) {
                sent++;
            }
        }

        return sent;
    }
}
