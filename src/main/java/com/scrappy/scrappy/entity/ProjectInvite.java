package com.scrappy.scrappy.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "project_invites")
public class ProjectInvite {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID inviteId = UUID.randomUUID();

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private String telegramUsername;

    @Column(nullable = false)
    private String role;

    private String position;

    @Column(nullable = false)
    private Date invitedAt = new Date();

    @Column(nullable = false)
    private Date expiresAt = new Date(System.currentTimeMillis() + 72 * 60 * 60 * 1000);
}