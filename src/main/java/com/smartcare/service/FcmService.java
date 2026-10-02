package com.smartcare.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    public String sendNotification(
            String token,
            String title,
            String body) {

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

            return FirebaseMessaging
                    .getInstance()
                    .send(message);

        } catch (Exception e) {

            return "FCM_ERROR: " + e.getMessage();
        }
    }
}
