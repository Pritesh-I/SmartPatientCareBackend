package com.smartcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {

        try {

            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            String path = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

            InputStream serviceAccount;

            if (path != null && !path.isBlank()) {
                serviceAccount = new FileInputStream(path);
            } else {
                serviceAccount =
                        new FileInputStream("firebase-service-account.json");
            }

            try (serviceAccount) {

                FirebaseOptions options =
                        FirebaseOptions.builder()
                                .setCredentials(
                                        GoogleCredentials.fromStream(
                                                serviceAccount))
                                .build();

                FirebaseApp.initializeApp(options);
            }

            System.out.println("🔥 Firebase Admin SDK initialized successfully.");

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to initialize Firebase Admin SDK.",
                    e);
        }
    }
}
