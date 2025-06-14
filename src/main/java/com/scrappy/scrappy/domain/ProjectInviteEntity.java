package com.scrappy.scrappy.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_invites")
@Data
public class ProjectInviteEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID inviteId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(name = "telegram_username", nullable = false)
    private String telegramUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    @Column
    private String position;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
        expiresAt = invitedAt.plusHours(72);
    }
}