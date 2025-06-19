package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserIdAndDate(Long userId, LocalDate date);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, TaskEntity.Status status);
    List<TaskEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<TaskEntity> findByUserId(Long userId);
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.date = :date")
    List<TaskEntity> findByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}