package com.scrappy.scrappy.controller.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteCreateDTO {
    @NotBlank(message = "Telegram username is required")
    private String telegramUsername;

    @NotBlank(message = "Role is required")
    private String role;

    private String position;
}