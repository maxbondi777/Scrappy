package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.ProjectEntity;
import com.scrappy.scrappy.domain.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<ShiftEntity, UUID> {
    List<ShiftEntity> findByProjectAndDateBetween(ProjectEntity project, LocalDate startDate, LocalDate endDate);
}