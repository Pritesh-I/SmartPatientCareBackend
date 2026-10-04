package com.smartcare.controller;

import com.smartcare.model.WebNotification;
import com.smartcare.repository.WebNotificationRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/web-notifications")
@CrossOrigin(origins="*")
public class WebNotificationController {

    private final WebNotificationRepository repo;

    public WebNotificationController(WebNotificationRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/user/{userId}")
    public Object get(@PathVariable Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/user/{userId}/unread-count")
    public Object count(@PathVariable Long userId) {
        return Map.of("count", repo.countByUserIdAndReadStatusFalse(userId));
    }

    @PostMapping
    public Object create(@RequestBody WebNotification n) {
        n.setReadStatus(false);
        return repo.save(n);
    }

    @PutMapping("/{id}/read")
    public Object read(@PathVariable Long id) {
        WebNotification n = repo.findById(id).orElseThrow();
        n.setReadStatus(true);
        return repo.save(n);
    }

    @PutMapping("/user/{userId}/read-all")
    public Object readAll(@PathVariable Long userId) {
        repo.findByUserIdOrderByCreatedAtDesc(userId).forEach(n -> {
            n.setReadStatus(true);
            repo.save(n);
        });
        return Map.of("success", true);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        repo.deleteById(id);
        return Map.of("success", true);
    }
}
