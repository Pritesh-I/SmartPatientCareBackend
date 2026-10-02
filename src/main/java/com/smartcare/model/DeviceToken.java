package com.smartcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 2048)
    private String token;

    private String platform;

    private LocalDateTime createdAt;

    public DeviceToken() {
    }

    public DeviceToken(Long userId,
                       String token,
                       String platform,
                       LocalDateTime createdAt) {
        this.userId = userId;
        this.token = token;
        this.platform = platform;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
