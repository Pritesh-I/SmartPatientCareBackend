package com.smartcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String notes;
    private String prescription;
    private LocalDateTime consultationDate;

    public Consultation() {}

    public Consultation(Long patientId, Long doctorId, String diagnosis,
                        String notes, String prescription,
                        LocalDateTime consultationDate) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.prescription = prescription;
        this.consultationDate = consultationDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public LocalDateTime getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDateTime consultationDate) {
        this.consultationDate = consultationDate;
    }
}
