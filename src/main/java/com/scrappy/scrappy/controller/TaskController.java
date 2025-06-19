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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = {"https://localhost:5173", "https://192.168.1.186:5173"}, allowCredentials = "true")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO,
                                                           @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received POST /tasks with TaskCreateDTO: {}, userId: {}", taskCreateDTO, userId);
        TaskDTO taskDTO = taskService.createTask(taskCreateDTO, userId);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks(@RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received GET /tasks for userId: {}", userId);
        List<TaskDTO> tasks = taskService.getAllTasks(userId);
        ApiResponse<List<TaskDTO>> response = new ApiResponse<>(tasks, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable Long id,
                                                            @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received GET /tasks/{} for userId: {}", id, userId);
        TaskDTO taskDTO = taskService.getTaskById(id, userId);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO,
                                                           @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received PUT /tasks/{} with TaskUpdateDTO: {}, userId: {}", id, taskUpdateDTO, userId);
        TaskDTO taskDTO = taskService.updateTask(id, taskUpdateDTO, userId);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO statusUpdateDTO,
                                                                 @RequestHeader("X-User-Id") Long userId) {
        logger.info("Received PATCH /tasks/{}/status with body: {}, userId: {}", id, statusUpdateDTO, userId);
        if (statusUpdateDTO == null || statusUpdateDTO.getStatus() == null) {
            logger.error("TaskStatusUpdateDTO is null or status is missing");
            throw new IllegalArgumentException("Request body or status is missing");
        }
        TaskDTO taskDTO = taskService.updateTaskStatus(id, statusUpdateDTO, userId);
        ApiResponse<TaskDTO> response = new ApiResponse<>(taskDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id,
                                                        @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received DELETE /tasks/{} for userId: {}", id, userId);
        taskService.deleteTask(id, userId);
        ApiResponse<Void> response = new ApiResponse<>(null, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/by-date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByDate(@PathVariable String date,
                                                                     @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received GET /tasks/by-date/{} for userId: {}", date, userId);
        List<TaskDTO> tasks = taskService.getTasksByDate(date, userId);
        ApiResponse<List<TaskDTO>> response = new ApiResponse<>(tasks, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TaskStatisticsDTO>> getTaskStatistics(@RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received GET /tasks/statistics for userId: {}", userId);
        TaskStatisticsDTO statistics = taskService.getTaskStatistics(userId);
        ApiResponse<TaskStatisticsDTO> response = new ApiResponse<>(statistics, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasks(@PathVariable Long userId,
                                                               @RequestHeader("X-User-Id") Long requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new IllegalArgumentException("Access denied: User ID mismatch");
        }
        List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
        return new ResponseEntity<>(new ApiResponse<>(tasks, null), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>(null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Void> response = new ApiResponse<>(null, "Validation failed: " + errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>(null, "Internal server error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}