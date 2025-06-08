package com.scrappy.scrappy.service;


import com.scrappy.scrappy.controller.dto.TaskCreateDTO;
import com.scrappy.scrappy.controller.dto.TaskDTO;
import com.scrappy.scrappy.controller.dto.TaskStatusUpdateDTO;
import com.scrappy.scrappy.controller.dto.TaskUpdateDTO;
import com.scrappy.scrappy.domain.Task;
import com.scrappy.scrappy.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        logger.debug("Creating task with DTO: {}", taskCreateDTO);
        Task task = taskMapper.toEntity(taskCreateDTO, 1L); // Фиксированный userId=1
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        logger.debug("Fetching all tasks");
        return taskRepository.findAll().stream()
                .filter(task -> task.getUserId().equals(1L))
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        logger.debug("Fetching task with id: {}", id);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(1L))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        logger.debug("Updating task with id: {}", id);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(1L))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskMapper.updateEntity(taskUpdateDTO, task);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatusUpdateDTO statusUpdateDTO) {
        logger.debug("Updating status for task with id: {}", id);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(1L))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        task.setStatus(Task.Status.valueOf(statusUpdateDTO.getStatus().toUpperCase()));
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        logger.debug("Deleting task with id: {}", id);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(1L))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or not owned by user"));
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByDate(String date) {
        logger.debug("Fetching tasks for date: {}", date);
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return taskRepository.findByUserIdAndDate(1L, localDate).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}