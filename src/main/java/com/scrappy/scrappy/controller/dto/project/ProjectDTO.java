package com.scrappy.scrappy.controller.dto.project;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectDTO {
    private UUID id;
    private String name;
    private String description;
    private String category;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId;
    private String currentUserRole;
}