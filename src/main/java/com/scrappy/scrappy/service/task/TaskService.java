package com.scrappy.scrappy.service.task;


import com.scrappy.scrappy.controller.dto.task.*;
import com.scrappy.scrappy.controller.dto.weeklyprogress.WeeklyProgressDTO;
import com.scrappy.scrappy.domain.TaskEntity;
import com.scrappy.scrappy.domain.UserEntity;
import com.scrappy.scrappy.repository.TaskRepository;
import com.scrappy.scrappy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskDTO createTask(TaskCreateDTO taskCreateDTO, Long telegramId) {
        logger.debug("Creating task with DTO: {}, telegramId: {}", taskCreateDTO, telegramId);
        try {
            TaskEntity task = taskMapper.toEntity(taskCreateDTO, telegramId);
            TaskEntity savedTask = taskRepository.save(task);
            logger.info("Task created successfully for telegramId: {}", telegramId);
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
    public List<TaskDTO> getAllTasks(Long telegramId) {
        logger.debug("Fetching all tasks for telegramId: {}", telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        return taskRepository.findByUserId(user.getId()).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id, Long telegramId) {
        logger.debug("Fetching task with id: {}, telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO, Long telegramId) {
        logger.debug("Updating task with id: {}, telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskMapper.updateEntity(taskUpdateDTO, task);
        TaskEntity updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatusUpdateDTO statusUpdateDTO, Long telegramId) {
        logger.debug("Updating status for task with id: {}, telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        task.setStatus(TaskEntity.Status.valueOf(statusUpdateDTO.getStatus().toUpperCase()));
        TaskEntity updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, Long telegramId) {
        logger.debug("Deleting task with id: {}, telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        TaskEntity task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByDate(String date, Long telegramId) {
        logger.debug("Fetching tasks for date: {}, telegramId: {}", date, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        LocalDate localDate = LocalDate.parse(date, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        return taskRepository.findByUserAndDate(user.getId(), localDate).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskStatisticsDTO getTaskStatistics(Long telegramId) {
        logger.debug("Fetching task statistics for telegramId: {}", telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        long totalTasks = taskRepository.countByUserId(user.getId());
        long completed = taskRepository.countByUserIdAndStatus(user.getId(), TaskEntity.Status.COMPLETED);
        long inProgress = taskRepository.countByUserIdAndStatus(user.getId(), TaskEntity.Status.IN_PROGRESS);
        double completionRate = totalTasks == 0 ? 0.0 : Math.round((completed / (double) totalTasks) * 100.0);

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        List<TaskEntity> weeklyTasks = taskRepository.findByUserIdAndDateBetween(user.getId(), monday, sunday);

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

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByUserId(Long telegramId) {
        logger.debug("Fetching tasks by telegramId: {}", telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        List<TaskEntity> tasks = taskRepository.findByUserId(user.getId());
        logger.info("Found {} tasks for telegramId: {}", tasks.size(), telegramId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}