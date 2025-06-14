package com.scrappy.scrappy.controller.dto.project;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MemberDTO {
    private Long userId;
    private String telegramUsername;
    private String role;
    private String position;
    private LocalDateTime joinedAt;
}