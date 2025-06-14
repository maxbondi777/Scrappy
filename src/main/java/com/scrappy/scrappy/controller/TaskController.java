package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.*;
import com.scrappy.scrappy.controller.dto.task.*;
import com.scrappy.scrappy.service.task.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        logger.debug("Received POST /tasks with TaskCreateDTO: {}", taskCreateDTO);
        TaskDTO taskDTO = taskService.createTask(taskCreateDTO);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        logger.debug("Received GET /tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        ApiResponse<List<TaskDTO>> response = new ApiResponse<>(tasks, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable Long id) {
        logger.debug("Received GET /tasks/{}", id);
        TaskDTO taskDTO = taskService.getTaskById(id);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        logger.debug("Received PUT /tasks/{} with TaskUpdateDTO: {}", id, taskUpdateDTO);
        TaskDTO taskDTO = taskService.updateTask(id, taskUpdateDTO);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO statusUpdateDTO) {
        logger.info("Received PATCH /tasks/{}/status with body: {}", id, statusUpdateDTO);
        if (statusUpdateDTO == null || statusUpdateDTO.getStatus() == null) {
            logger.error("TaskStatusUpdateDTO is null or status is missing");
            throw new IllegalArgumentException("Request body or status is missing");
        }
        TaskDTO taskDTO = taskService.updateTaskStatus(id, statusUpdateDTO);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        logger.debug("Received DELETE /tasks/{}", id);
        taskService.deleteTask(id);
        ApiResponse<Void> response = new ApiResponse<>(null, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/by-date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByDate(@PathVariable String date) {
        logger.debug("Received GET /tasks/by-date/{}", date);
        List<TaskDTO> tasks = taskService.getTasksByDate(date);
        ApiResponse<List<TaskDTO>> response = new ApiResponse<>(tasks, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskStatisticsDTO>> getTaskStatistics() {
        logger.debug("Received GET /tasks/statistics");
        TaskStatisticsDTO statistics = taskService.getTaskStatistics();
        ApiResponse<TaskStatisticsDTO> response = new ApiResponse<>(statistics, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}