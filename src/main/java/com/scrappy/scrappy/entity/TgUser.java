package com.scrappy.scrappy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "tg_users")
public class TgUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long telegramId;

    private Date authDate;
    private String hash;
    private String queryId;
    private String signature;
    private boolean allowsWriteToPm;
    private String firstName;
    private String lastName;
    private String languageCode;
    private String photoUrl;
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subscription subscription;

    public enum Subscription {
        FREE, PREMIUM
    }
}