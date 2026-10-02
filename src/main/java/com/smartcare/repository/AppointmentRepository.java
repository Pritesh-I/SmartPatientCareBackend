package com.smartcare.repository;

import com.smartcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeAsc(
            Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(
            Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(
            Long doctorId, LocalDate appointmentDate);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String status);
}
