package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByProjectIdAndDateBetween(UUID projectId, LocalDateTime start, LocalDateTime end);
}