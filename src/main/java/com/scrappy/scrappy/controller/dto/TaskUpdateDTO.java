package com.scrappy.scrappy.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TaskUpdateDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in YYYY-MM-DD format")
    private String date;

    private Boolean allDay;

    @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Start time must be in HH:MM:SS format")
    private String startTime;

    @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "End time must be in HH:MM:SS format")
    private String endTime;

    private String location;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #4287f5)")
    private String color;

    private String reminder;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Status is required")
    private String status;
}