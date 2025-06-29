package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.Task;
import com.scrappy.scrappy.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping
    public ResponseEntity<Iterable<Task>> getTasks(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestHeader("X-User-Id") Long userId, @RequestBody Task task) {
        task.setUserId(userId);
        task.setDate(new Date());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId, @RequestBody Task task) {
        return taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId))
                .map(t -> {
                    t.setStatus(task.getStatus());
                    t.setDate(task.getDate());
                    return ResponseEntity.ok(taskRepository.save(t));
                }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId, @RequestBody Map<String, String> status) {
        return taskRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId))
                .map(t -> {
                    t.setStatus(Task.Status.valueOf(status.get("status")));
                    return ResponseEntity.ok(taskRepository.save(t));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUserId().equals(userId))
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(@RequestHeader("X-User-Id") Long userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        long completed = taskRepository.countByUserIdAndStatus(userId, Task.Status.COMPLETED);
        long inProgress = taskRepository.countByUserIdAndStatus(userId, Task.Status.PENDING);
        double completionRate = totalTasks == 0 ? 0 : Math.round((completed / (double) totalTasks) * 100);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY).plusDays(1);

        Map<String, Map<String, Long>> weeklyProgress = new HashMap<>();
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            LocalDate dayStart = startOfWeek.plusDays(day.getValue() - 1);
            Date dateStart = Date.from(dayStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dateEnd = Date.from(dayStart.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            long dayTotal = taskRepository.findByUserIdAndDateBetween(userId, dateStart, dateEnd).size();
            long dayCompleted = taskRepository.findByUserIdAndDateBetween(userId, dateStart, dateEnd)
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