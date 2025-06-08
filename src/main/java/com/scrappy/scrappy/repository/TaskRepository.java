package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDate(Long userId, LocalDate date);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Task.Status status);
    List<Task> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}