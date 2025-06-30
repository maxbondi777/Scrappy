package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    List<ProjectMember> findByProjectId(UUID projectId);
    Optional<ProjectMember> findByUserId(Long userId); // Новый метод для поиска по userId
}