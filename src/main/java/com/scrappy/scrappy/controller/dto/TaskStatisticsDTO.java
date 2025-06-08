package com.scrappy.scrappy.controller.dto;

import lombok.Data;

@Data
public class TaskStatisticsDTO {
    private long totalTasks;
    private long completed;
    private long inProgress;
    private double completionRate;
    private WeeklyProgressDTO weeklyProgress;
}