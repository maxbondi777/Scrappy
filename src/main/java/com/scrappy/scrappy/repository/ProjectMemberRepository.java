package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.ProjectEntity;
import com.scrappy.scrappy.domain.ProjectMemberEntity;
import com.scrappy.scrappy.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, UUID> {
    List<ProjectMemberEntity> findByProject(ProjectEntity project);
    Optional<ProjectMemberEntity> findByProjectAndUser(ProjectEntity project, UserEntity user);
}