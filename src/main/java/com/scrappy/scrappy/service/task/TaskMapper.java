package com.scrappy.scrappy.service.task;

import com.scrappy.scrappy.controller.dto.task.TaskCreateDTO;
import com.scrappy.scrappy.controller.dto.task.TaskDTO;
import com.scrappy.scrappy.controller.dto.task.TaskUpdateDTO;
import com.scrappy.scrappy.domain.TaskEntity;
import com.scrappy.scrappy.domain.UserEntity;
import com.scrappy.scrappy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TaskMapper {

    private static final Logger logger = LoggerFactory.getLogger(TaskMapper.class);
    private final UserRepository userRepository;

    public TaskMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TaskEntity toEntity(TaskCreateDTO dto, Long userId) {
        logger.debug("Mapping TaskCreateDTO to Task for userId: {}", userId);
        TaskEntity task = new TaskEntity();

        // Загружаем пользователя из базы
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found with ID: " + userId);
                });

        task.setUser(user); // Привязываем загруженного пользователя
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDate(LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        task.setAllDay(dto.getAllDay() != null ? dto.getAllDay() : false);
        task.setStartTime(dto.getStartTime() != null ? LocalTime.parse(dto.getStartTime()) : null);
        task.setEndTime(dto.getEndTime() != null ? LocalTime.parse(dto.getEndTime()) : null);
        task.setLocation(dto.getLocation());
        task.setColor(dto.getColor());
        String reminder = dto.getReminder() != null ? dto.getReminder().toUpperCase() : "NONE";
        reminder = normalizeReminder(reminder);
        task.setReminder(TaskEntity.Reminder.valueOf(reminder));
        task.setPriority(TaskEntity.Priority.valueOf(dto.getPriority().toUpperCase()));
        task.setCategory(TaskEntity.Category.valueOf(dto.getCategory().toUpperCase()));
        return task;
    }

    public TaskDTO toDto(TaskEntity task) {
        logger.debug("Mapping Task to TaskDTO for taskId: {}", task.getId());
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUser() != null ? task.getUser().getId() : null); // Получаем ID пользователя
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDate(task.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setAllDay(task.isAllDay());
        dto.setStartTime(task.getStartTime() != null ? task.getStartTime().toString() : null);
        dto.setEndTime(task.getEndTime() != null ? task.getEndTime().toString() : null);
        dto.setLocation(task.getLocation());
        dto.setColor(task.getColor());
        String reminder = task.getReminder().name().toLowerCase();
        reminder = denormalizeReminder(reminder);
        dto.setReminder(reminder);
        dto.setPriority(task.getPriority().name().toLowerCase());
        dto.setCategory(task.getCategory().name().toLowerCase());
        dto.setStatus(task.getStatus().name().toLowerCase());
        dto.setCreatedAt(task.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    public void updateEntity(TaskUpdateDTO dto, TaskEntity task) {
        logger.debug("Updating Task from TaskUpdateDTO for taskId: {}", task.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDate(LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        task.setAllDay(dto.getAllDay() != null ? dto.getAllDay() : false);
        task.setStartTime(dto.getStartTime() != null ? LocalTime.parse(dto.getStartTime()) : null);
        task.setEndTime(dto.getEndTime() != null ? LocalTime.parse(dto.getEndTime()) : null);
        task.setLocation(dto.getLocation());
        task.setColor(dto.getColor());
        String reminder = dto.getReminder() != null ? dto.getReminder().toUpperCase() : "NONE";
        reminder = normalizeReminder(reminder);
        task.setReminder(TaskEntity.Reminder.valueOf(reminder));
        task.setPriority(TaskEntity.Priority.valueOf(dto.getPriority().toUpperCase()));
        task.setCategory(TaskEntity.Category.valueOf(dto.getCategory().toUpperCase()));
        task.setStatus(TaskEntity.Status.valueOf(dto.getStatus().toUpperCase()));
    }

    private String normalizeReminder(String reminder) {
        return reminder.replace("5M", "M5").replace("15M", "M15").replace("30M", "M30").replace("1H", "H1").replace("1D", "D1");
    }

    private String denormalizeReminder(String reminder) {
        return reminder.replace("m5", "5m").replace("m15", "15m").replace("m30", "30m").replace("h1", "1h").replace("d1", "1d");
    }
}