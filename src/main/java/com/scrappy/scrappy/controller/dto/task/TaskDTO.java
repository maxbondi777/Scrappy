package com.scrappy.scrappy.controller.dto.task;

import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String date;
    private boolean allDay;
    private String startTime;
    private String endTime;
    private String location;
    private String color;
    private String reminder;
    private String priority;
    private String category;
    private String status;
    private String createdAt;
}