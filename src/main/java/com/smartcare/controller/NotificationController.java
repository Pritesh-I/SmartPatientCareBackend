package com.smartcare.controller;

import com.smartcare.model.DeviceToken;
import com.smartcare.repository.DeviceTokenRepository;
import com.smartcare.service.FcmService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final DeviceTokenRepository repository;
    private final FcmService fcmService;

    public NotificationController(
            DeviceTokenRepository repository,
            FcmService fcmService) {

        this.repository = repository;
        this.fcmService = fcmService;
    }

    @PostMapping("/device")
    public Object registerDevice(
            @RequestBody DeviceToken device) {

        if (device.getUserId() == null ||
                device.getToken() == null ||
                device.getToken().isBlank()) {

            return Map.of(
                    "success", false,
                    "message", "User ID and FID are required."
            );
        }

        DeviceToken existing =
                repository.findByUserIdAndToken(
                        device.getUserId(),
                        device.getToken()
                ).orElse(null);

        if (existing != null) {
            existing.setPlatform(device.getPlatform());
            existing.setCreatedAt(LocalDateTime.now());

            return repository.save(existing);
        }

        device.setCreatedAt(LocalDateTime.now());

        return repository.save(device);
    }

    @GetMapping("/user/{userId}")
    public List<DeviceToken> getDevices(
            @PathVariable Long userId) {

        return repository.findByUserId(userId);
    }

    @DeleteMapping("/device/{id}")
    public Object deleteDevice(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {

            return Map.of(
                    "success", false,
                    "message", "Device not found."
            );
        }

        repository.deleteById(id);

        return Map.of(
                "success", true,
                "message", "Device removed successfully."
        );
    }

    @PostMapping("/send")
    public Object sendNotification(
            @RequestBody Map<String, String> request) {

        String fid = request.get("fid");
        String title = request.get("title");
        String body = request.get("body");

        if (fid == null || fid.isBlank()) {

            return Map.of(
                    "success", false,
                    "message", "Firebase Installation ID is required."
            );
        }

        if (title == null || title.isBlank()) {
            title = "SmartPatientCare";
        }

        if (body == null || body.isBlank()) {
            body = "You have a new notification.";
        }

        String result =
                fcmService.sendNotification(fid, title, body);

        if (result.startsWith("FCM_ERROR")) {

            return Map.of(
                    "success", false,
                    "message", result
            );
        }

        return Map.of(
                "success", true,
                "message", "Notification sent successfully.",
                "firebaseMessageId", result
        );
    }
}
