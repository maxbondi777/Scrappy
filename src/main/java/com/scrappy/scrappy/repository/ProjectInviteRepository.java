package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, UUID> {
    List<ProjectInvite> findByProjectId(UUID projectId);
}