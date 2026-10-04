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

    // =========================================================
    // CREATE TOKEN
    // Token numbers are now separate for each doctor.
    // =========================================================

    @PostMapping("/token")
    public QueueToken createToken(@RequestBody QueueToken token) {

        LocalDate today = LocalDate.now();

        if (token.getDoctorId() == null) {
            throw new IllegalArgumentException(
                    "Doctor ID is required for queue token."
            );
        }

        List<QueueToken> doctorQueue =
                repository.findByDoctorIdAndQueueDateOrderByTokenNumberAsc(
                        token.getDoctorId(),
                        today
                );

        int nextToken = doctorQueue.stream()
                .mapToInt(QueueToken::getTokenNumber)
                .max()
                .orElse(0) + 1;

        token.setTokenNumber(nextToken);
        token.setQueueDate(today);
        token.setCreatedAt(LocalDateTime.now());

        if (token.getStatus() == null ||
                token.getStatus().isBlank()) {

            token.setStatus("WAITING");
        }

        if (token.getRoom() == null ||
                token.getRoom().isBlank()) {

            token.setRoom("Room 1");
        }

        return repository.save(token);
    }

    // =========================================================
    // TODAY'S COMPLETE QUEUE
    // =========================================================

    @GetMapping("/today")
    public List<QueueToken> todayQueue() {

        return repository.findByQueueDateOrderByTokenNumberAsc(
                LocalDate.now()
        );
    }

    // =========================================================
    // TODAY'S QUEUE FOR ONE DOCTOR
    // =========================================================

    @GetMapping("/doctor/{doctorId}")
    public List<QueueToken> doctorQueue(
            @PathVariable Long doctorId) {

        return repository
                .findByDoctorIdAndQueueDateOrderByTokenNumberAsc(
                        doctorId,
                        LocalDate.now()
                );
    }

    // =========================================================
    // PATIENT QUEUE
    // =========================================================

    @GetMapping("/patient/{patientId}")
    public List<QueueToken> patientQueue(
            @PathVariable Long patientId) {

        return repository.findByPatientIdAndQueueDate(
                patientId,
                LocalDate.now()
        );
    }

    // =========================================================
    // UPDATE TOKEN STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public QueueToken updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        QueueToken token =
                repository.findById(id).orElse(null);

        if (token == null) {
            return null;
        }

        String newStatus = status.toUpperCase();

        token.setStatus(newStatus);

        return repository.save(token);
    }

    // =========================================================
    // PATIENT QUEUE STATUS
    // =========================================================

    @GetMapping("/patient/{patientId}/status")
    public Map<String, Object> patientQueueStatus(
            @PathVariable Long patientId) {

        Map<String, Object> response =
                new HashMap<>();

        List<QueueToken> patientTokens =
                repository.findByPatientIdAndQueueDate(
                        patientId,
                        LocalDate.now()
                );

        if (patientTokens.isEmpty()) {

            response.put(
                    "message",
                    "No token found for today."
            );

            return response;
        }

        // Use latest token for this patient today.
        QueueToken patientToken =
                patientTokens.get(patientTokens.size() - 1);

        Long doctorId =
                patientToken.getDoctorId();

        // -----------------------------------------------------
        // IMPORTANT:
        // Only this doctor's queue is considered.
        // -----------------------------------------------------

        List<QueueToken> doctorQueue =
                repository
                        .findByDoctorIdAndQueueDateOrderByTokenNumberAsc(
                                doctorId,
                                LocalDate.now()
                        );

        // Current token being served by THIS doctor.
        int currentToken = doctorQueue.stream()
                .filter(q ->
                        "SERVING".equals(q.getStatus()) ||
                        "IN_CONSULTATION".equals(q.getStatus()) ||
                        "CALLED".equals(q.getStatus())
                )
                .mapToInt(QueueToken::getTokenNumber)
                .max()
                .orElse(0);

        // Patients ahead only in THIS doctor's queue.
        long ahead = doctorQueue.stream()
                .filter(q ->
                        q.getTokenNumber()
                                < patientToken.getTokenNumber()
                )
                .filter(q ->
                        !"COMPLETED".equals(q.getStatus()) &&
                        !"CANCELLED".equals(q.getStatus())
                )
                .count();

        response.put(
                "tokenNumber",
                patientToken.getTokenNumber()
        );

        response.put(
                "doctorId",
                patientToken.getDoctorId()
        );

        response.put(
                "currentToken",
                currentToken
        );

        response.put(
                "patientsAhead",
                ahead
        );

        response.put(
                "status",
                patientToken.getStatus()
        );

        response.put(
                "room",
                patientToken.getRoom()
        );

        // -----------------------------------------------------
        // Patient notification message
        // -----------------------------------------------------

        if ("CALLED".equals(patientToken.getStatus())) {

            response.put(
                    "notification",
                    "Your token has been called. Please proceed to "
                            + patientToken.getRoom() + "."
            );

        } else if (
                ahead <= 2 &&
                !"COMPLETED".equals(patientToken.getStatus()) &&
                !"CANCELLED".equals(patientToken.getStatus())
        ) {

            response.put(
                    "notification",
                    "Your token is approaching."
            );

        } else {

            response.put(
                    "notification",
                    "Please wait for your token."
            );
        }

        return response;
    }
}
