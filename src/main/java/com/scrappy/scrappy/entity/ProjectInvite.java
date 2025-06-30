package com.scrappy.scrappy.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "project_invites")
public class ProjectInvite {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String telegramUsername;

    @Column(nullable = false)
    private String role;

    private String position;

    @Column(name = "invited_at", updatable = false)
    private LocalDateTime invitedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
        expiresAt = invitedAt.plusHours(72);
    }
}