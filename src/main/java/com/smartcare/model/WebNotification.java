package com.smartcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class WebNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;

    @Column(length = 1000)
    private String message;

    private boolean readStatus = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public WebNotification() {}

    public WebNotification(Long userId, String title, String message) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.readStatus = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId(){ return id; }
    public Long getUserId(){ return userId; }
    public String getTitle(){ return title; }
    public String getMessage(){ return message; }
    public boolean isReadStatus(){ return readStatus; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    public void setUserId(Long v){ userId=v; }
    public void setTitle(String v){ title=v; }
    public void setMessage(String v){ message=v; }
    public void setReadStatus(boolean v){ readStatus=v; }
    public void setCreatedAt(LocalDateTime v){ createdAt=v; }
}
