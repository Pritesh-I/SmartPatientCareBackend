package com.smartcare.controller;

import com.smartcare.model.Appointment;
import com.smartcare.model.UserAccount;
import com.smartcare.model.DoctorAvailability;
import com.smartcare.repository.AppointmentRepository;
import com.smartcare.repository.DoctorAvailabilityRepository;
import com.smartcare.repository.UserAccountRepository;
import com.smartcare.service.FcmService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final UserAccountRepository userAccountRepository;
    private final FcmService fcmService;

    public AppointmentController(
            AppointmentRepository appointmentRepository,
            DoctorAvailabilityRepository availabilityRepository,
            UserAccountRepository userAccountRepository,
            FcmService fcmService) {

        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.userAccountRepository = userAccountRepository;
        this.fcmService = fcmService;
    }

    @PostMapping
    public Object createAppointment(
            @RequestBody Appointment appointment) {

        if (appointment.getPatientId() == null ||
                appointment.getDoctorId() == null ||
                appointment.getAppointmentDate() == null ||
                appointment.getAppointmentTime() == null) {

            return Map.of(
                    "success", false,
                    "message", "Patient, doctor, date and time are required."
            );
        }

        if (appointment.getAppointmentDate()
                .isBefore(LocalDate.now())) {

            return Map.of(
                    "success", false,
                    "message", "Appointment date cannot be in the past."
            );
        }

        DayOfWeek day =
                appointment.getAppointmentDate().getDayOfWeek();

        String dayName = day.name();

        List<DoctorAvailability> availability =
                availabilityRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(
                        appointment.getDoctorId());

        boolean slotAvailable = availability.stream()
                .anyMatch(a ->
                        a.isAvailable()
                        && dayName.equalsIgnoreCase(a.getDayOfWeek())
                        && !LocalTime.parse(a.getStartTime())
                                .isAfter(appointment.getAppointmentTime())
                        && !LocalTime.parse(a.getEndTime())
                                .isBefore(appointment.getAppointmentTime())
                );

        if (!slotAvailable) {
            return Map.of(
                    "success", false,
                    "message", "Doctor is not available at the selected date and time."
            );
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                appointment.getDoctorId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                "CANCELLED"
                        );

        if (alreadyBooked) {
            return Map.of(
                    "success", false,
                    "message", "This appointment slot is already booked."
            );
        }

        if (appointment.getStatus() == null ||
                appointment.getStatus().isBlank()) {

            appointment.setStatus("PENDING");
        }

        appointment.setStatus(
                appointment.getStatus().toUpperCase()
        );

        Appointment saved =
                appointmentRepository.save(appointment);

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Appointment booked successfully.");
        response.put("appointment", saved);

        return response;
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientAppointments(
            @PathVariable Long patientId) {

        return appointmentRepository
                .findByPatientIdOrderByAppointmentDateDescAppointmentTimeAsc(
                        patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getDoctorAppointments(
            @PathVariable Long doctorId) {

        return appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(
                        doctorId);
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public List<Appointment> getDoctorAppointmentsByDate(
            @PathVariable Long doctorId,
            @PathVariable String date) {

        return appointmentRepository
                .findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(
                        doctorId,
                        LocalDate.parse(date));
    }

    @GetMapping("/doctor/{doctorId}/available-slots")
    public List<String> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam String date) {

        LocalDate appointmentDate =
                LocalDate.parse(date);

        DayOfWeek day =
                appointmentDate.getDayOfWeek();

        String dayName = day.name();

        List<DoctorAvailability> availability =
                availabilityRepository
                        .findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(
                                doctorId);

        List<Appointment> booked =
                appointmentRepository
                        .findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(
                                doctorId,
                                appointmentDate);

        List<String> slots = new ArrayList<>();

        for (DoctorAvailability a : availability) {

            if (!a.isAvailable() ||
                    !dayName.equalsIgnoreCase(a.getDayOfWeek())) {
                continue;
            }

            LocalTime start =
                    LocalTime.parse(a.getStartTime());

            LocalTime end =
                    LocalTime.parse(a.getEndTime());

            LocalTime current = start;

            while (current.isBefore(end)) {

                LocalTime slot = current;

                boolean bookedSlot = booked.stream()
                        .anyMatch(b ->
                                slot.equals(b.getAppointmentTime())
                                && !"CANCELLED".equals(b.getStatus()));

                if (!bookedSlot) {
                    slots.add(slot.toString());
                }

                current = current.plusMinutes(30);
            }
        }

        return slots;
    }

    @PutMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Appointment appointment =
                appointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            return Map.of(
                    "success", false,
                    "message", "Appointment not found."
            );
        }

        String newStatus =
                status.toUpperCase();

        List<String> validStatuses =
                List.of(
                        "PENDING",
                        "CONFIRMED",
                        "CANCELLED",
                        "COMPLETED"
                );

        if (!validStatuses.contains(newStatus)) {
            return Map.of(
                    "success", false,
                    "message",
                    "Invalid status. Use PENDING, CONFIRMED, CANCELLED or COMPLETED."
            );
        }

        appointment.setStatus(newStatus);

        Appointment saved =
                appointmentRepository.save(appointment);

        int notificationsSent = 0;

        if ("CONFIRMED".equals(newStatus) &&
                appointment.getPatientId() != null) {

            UserAccount user =
                    userAccountRepository.findAll()
                            .stream()
                            .filter(u ->
                                    "PATIENT".equalsIgnoreCase(u.getRole()) &&
                                    appointment.getPatientId().equals(u.getProfileId()))
                            .findFirst()
                            .orElse(null);

            if (user != null) {
                notificationsSent =
                        fcmService.sendToUser(
                                user.getId(),
                                "Appointment Confirmed",
                                "Your appointment has been confirmed. Please check Smart Patient Care for your appointment and token details."
                        );
            }
        }

        return Map.of(
                "success", true,
                "message", "Appointment status updated.",
                "notificationsSent", notificationsSent,
                "appointment", saved
        );
    }

    @DeleteMapping("/{id}")
    public Object deleteAppointment(
            @PathVariable Long id) {

        Appointment appointment =
                appointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            return Map.of(
                    "success", false,
                    "message", "Appointment not found."
            );
        }

        appointment.setStatus("CANCELLED");

        appointmentRepository.save(appointment);

        return Map.of(
                "success", true,
                "message", "Appointment cancelled successfully."
        );
    }
}
