package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.ProjectEntity;
import com.scrappy.scrappy.domain.ProjectInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectInviteRepository extends JpaRepository<ProjectInviteEntity, UUID> {
    List<ProjectInviteEntity> findByProject(ProjectEntity project);
}