package com.scrappy.scrappy.service.task;


import com.scrappy.scrappy.controller.dto.task.*;
import com.scrappy.scrappy.controller.dto.weeklyprogress.WeeklyProgressDTO;
import com.scrappy.scrappy.domain.TaskEntity;
import com.scrappy.scrappy.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskDTO createTask(TaskCreateDTO taskCreateDTO, Long userId) {
        logger.debug("Creating task with DTO: {}, userId: {}", taskCreateDTO, userId);
        try {
            TaskEntity task = taskMapper.toEntity(taskCreateDTO, userId);
            TaskEntity savedTask = taskRepository.save(task);
            logger.info("Task created successfully for userId: {}", userId);
            return taskMapper.toDto(savedTask);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create task: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating task: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks(Long userId) {
        logger.debug("Fetching all tasks for userId: {}", userId);
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(userId))
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id, Long userId) {
        logger.debug("Fetching task with id: {}, userId: {}", id, userId);
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO, Long userId) {
        logger.debug("Updating task with id: {}, userId: {}", id, userId);
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskMapper.updateEntity(taskUpdateDTO, task);
        TaskEntity updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatusUpdateDTO statusUpdateDTO, Long userId) {
        logger.debug("Updating status for task with id: {}, userId: {}", id, userId);
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        task.setStatus(TaskEntity.Status.valueOf(statusUpdateDTO.getStatus().toUpperCase()));
        TaskEntity updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        logger.debug("Deleting task with id: {}, userId: {}", id, userId);
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByDate(String date, Long userId) {
        logger.debug("Fetching tasks for date: {}, userId: {}", date, userId);
        LocalDate localDate = LocalDate.parse(date, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        return taskRepository.findByUserAndDate(userId, localDate).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskStatisticsDTO getTaskStatistics(Long userId) {
        logger.debug("Fetching task statistics for userId: {}", userId);

        long totalTasks = taskRepository.countByUserId(userId);
        long completed = taskRepository.countByUserIdAndStatus(userId, TaskEntity.Status.COMPLETED);
        long inProgress = taskRepository.countByUserIdAndStatus(userId, TaskEntity.Status.IN_PROGRESS);
        double completionRate = totalTasks == 0 ? 0.0 : Math.round((completed / (double) totalTasks) * 100.0);

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        List<TaskEntity> weeklyTasks = taskRepository.findByUserIdAndDateBetween(userId, monday, sunday);

        WeeklyProgressDTO weeklyProgress = new WeeklyProgressDTO();
        Map<LocalDate, List<TaskEntity>> tasksByDate = weeklyTasks.stream()
                .collect(Collectors.groupingBy(TaskEntity::getDate));

        weeklyProgress.setMonday(getDayProgress(tasksByDate, monday));
        weeklyProgress.setTuesday(getDayProgress(tasksByDate, monday.plusDays(1)));
        weeklyProgress.setWednesday(getDayProgress(tasksByDate, monday.plusDays(2)));
        weeklyProgress.setThursday(getDayProgress(tasksByDate, monday.plusDays(3)));
        weeklyProgress.setFriday(getDayProgress(tasksByDate, monday.plusDays(4)));
        weeklyProgress.setSaturday(getDayProgress(tasksByDate, monday.plusDays(5)));
        weeklyProgress.setSunday(getDayProgress(tasksByDate, monday.plusDays(6)));

        TaskStatisticsDTO statistics = new TaskStatisticsDTO();
        statistics.setTotalTasks(totalTasks);
        statistics.setCompleted(completed);
        statistics.setInProgress(inProgress);
        statistics.setCompletionRate(completionRate);
        statistics.setWeeklyProgress(weeklyProgress);

        return statistics;
    }

    private WeeklyProgressDTO.DayProgressDTO getDayProgress(Map<LocalDate, List<TaskEntity>> tasksByDate, LocalDate date) {
        WeeklyProgressDTO.DayProgressDTO dayProgress = new WeeklyProgressDTO.DayProgressDTO();
        List<TaskEntity> tasks = tasksByDate.getOrDefault(date, List.of());
        long total = tasks.size();
        long completed = tasks.stream().filter(task -> task.getStatus() == TaskEntity.Status.COMPLETED).count();
        dayProgress.setTotal(total);
        dayProgress.setCompleted(completed);
        return dayProgress;
    }

    public List<TaskDTO> getTasksByUserId(Long userId) {
        List<TaskEntity> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}