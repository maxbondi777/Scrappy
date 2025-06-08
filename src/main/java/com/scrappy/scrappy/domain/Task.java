package com.scrappy.scrappy.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "all_day", nullable = false)
    private boolean allDay;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private String color;

    @Enumerated(EnumType.STRING)
    private Reminder reminder;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum Reminder {
        NONE, M5, M15, M30, H1, D1
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Category {
        PERSONAL, WORK, OTHER
    }

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = Status.PENDING;
        }
    }
}