package com.scrappy.scrappy.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "allows_write_to_pm")
    private Boolean allowsWriteToPm;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription", nullable = false)
    private Subscription subscription = Subscription.FREE;

    @Column(name = "hash")
    private String hash;

    @Column(name = "query_id")
    private String queryId;

    @Column(name = "signature")
    private String signature;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}