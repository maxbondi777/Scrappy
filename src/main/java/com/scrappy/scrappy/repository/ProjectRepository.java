package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.ProjectEntity;
import com.scrappy.scrappy.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findByOwner(UserEntity owner);
}