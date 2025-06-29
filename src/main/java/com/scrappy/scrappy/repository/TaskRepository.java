package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDateBetween(Long userId, Date startDate, Date endDate);
    Long countByUserIdAndStatus(Long userId, Task.Status status);
    Long countByUserId(Long userId);
}