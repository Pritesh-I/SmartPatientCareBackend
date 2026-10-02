package com.smartcare.controller;

import com.smartcare.model.QueueToken;
import com.smartcare.repository.QueueTokenRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueTokenRepository repository;

    public QueueController(QueueTokenRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/token")
    public QueueToken createToken(@RequestBody QueueToken token) {

        LocalDate today = LocalDate.now();

        List<QueueToken> todayQueue =
                repository.findByQueueDateOrderByTokenNumberAsc(today);

        int nextToken = todayQueue.stream()
                .mapToInt(QueueToken::getTokenNumber)
                .max()
                .orElse(0) + 1;

        token.setTokenNumber(nextToken);
        token.setQueueDate(today);
        token.setCreatedAt(LocalDateTime.now());

        if (token.getStatus() == null || token.getStatus().isBlank()) {
            token.setStatus("WAITING");
        }

        if (token.getRoom() == null || token.getRoom().isBlank()) {
            token.setRoom("Room 1");
        }

        return repository.save(token);
    }

    @GetMapping("/today")
    public List<QueueToken> todayQueue() {
        return repository.findByQueueDateOrderByTokenNumberAsc(
                LocalDate.now());
    }

    @GetMapping("/patient/{patientId}")
    public List<QueueToken> patientQueue(
            @PathVariable Long patientId) {

        return repository.findByPatientIdAndQueueDate(
                patientId, LocalDate.now());
    }

    @PutMapping("/{id}/status")
    public QueueToken updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        QueueToken token = repository.findById(id).orElse(null);

        if (token == null) {
            return null;
        }

        token.setStatus(status.toUpperCase());

        return repository.save(token);
    }

    @GetMapping("/patient/{patientId}/status")
    public Map<String, Object> patientQueueStatus(
            @PathVariable Long patientId) {

        List<QueueToken> tokens =
                repository.findByPatientIdAndQueueDate(
                        patientId, LocalDate.now());

        Map<String, Object> response = new HashMap<>();

        if (tokens.isEmpty()) {
            response.put("message", "No token found for today.");
            return response;
        }

        QueueToken patientToken = tokens.get(tokens.size() - 1);

        List<QueueToken> queue =
                repository.findByQueueDateOrderByTokenNumberAsc(
                        LocalDate.now());

        int currentToken = queue.stream()
                .filter(q -> "SERVING".equals(q.getStatus()))
                .mapToInt(QueueToken::getTokenNumber)
                .max()
                .orElse(0);

        long ahead = queue.stream()
                .filter(q -> q.getTokenNumber() < patientToken.getTokenNumber())
                .filter(q -> !"COMPLETED".equals(q.getStatus()))
                .count();

        response.put("tokenNumber", patientToken.getTokenNumber());
        response.put("currentToken", currentToken);
        response.put("patientsAhead", ahead);
        response.put("status", patientToken.getStatus());
        response.put("room", patientToken.getRoom());

        if ("CALLED".equals(patientToken.getStatus())) {
            response.put("notification",
                    "Your token has been called. Please proceed to " +
                    patientToken.getRoom() + ".");
        } else if (ahead <= 2 && !"COMPLETED".equals(patientToken.getStatus())) {
            response.put("notification",
                    "Your token is approaching.");
        } else {
            response.put("notification",
                    "Please wait for your token.");
        }

        return response;
    }
}
