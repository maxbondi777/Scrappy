package com.scrappy.scrappy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "username")
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "hash")
    private String hash;

    @Column(name = "query_id")
    private String queryId;

    @Column(name = "signature")
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription")
    private Subscription subscription;

    // Геттеры, сеттеры (если не используете Lombok)
}