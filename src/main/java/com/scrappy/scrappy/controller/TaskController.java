package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.Task;
import com.scrappy.scrappy.entity.User;
import com.scrappy.scrappy.repository.TaskRepository;
import com.scrappy.scrappy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Iterable<Task>> getTasks(@RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return ResponseEntity.ok(taskRepository.findByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestHeader("X-User-Id") Long telegramId, @RequestBody Task task) {
        User user = userService.getUser(String.valueOf(telegramId));
        task.setUser(user);
        task.setDate(LocalDateTime.now());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId, @RequestBody Task task) {
        User user = userService.getUser(String.valueOf(telegramId));
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> {
                    t.setStatus(task.getStatus());
                    t.setDate(task.getDate());
                    return ResponseEntity.ok(taskRepository.save(t));
                }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId, @RequestBody Map<String, String> status) {
        User user = userService.getUser(String.valueOf(telegramId));
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> {
                    t.setStatus(Task.Status.valueOf(status.get("status")));
                    return ResponseEntity.ok(taskRepository.save(t));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(@RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        long totalTasks = taskRepository.countByUserId(user.getId());
        long completed = taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.COMPLETED);
        long inProgress = taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.PENDING);
        double completionRate = totalTasks == 0 ? 0 : Math.round((completed / (double) totalTasks) * 100);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY).plusDays(1);

        Map<String, Map<String, Long>> weeklyProgress = new HashMap<>();
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            LocalDate dayStart = startOfWeek.plusDays(day.getValue() - 1);
            LocalDateTime dateStart = dayStart.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime dateEnd = dateStart.plusDays(1);
            long dayTotal = taskRepository.findByUserIdAndDateBetween(user.getId(), dateStart, dateEnd).size();
            long dayCompleted = taskRepository.findByUserIdAndDateBetween(user.getId(), dateStart, dateEnd)
                    .stream().filter(t -> t.getStatus() == Task.Status.COMPLETED).count();
            weeklyProgress.put(day.toString().toLowerCase(), Map.of("completed", dayCompleted, "total", dayTotal));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", totalTasks);
        response.put("completed", completed);
        response.put("inProgress", inProgress);
        response.put("completionRate", completionRate);
        response.put("weeklyProgress", weeklyProgress);

        return ResponseEntity.ok(response);
    }
}