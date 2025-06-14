package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserIdAndDate(Long userId, LocalDate date);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, TaskEntity.Status status);
    List<TaskEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}