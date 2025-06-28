package com.scrappy.scrappy.service.project;

import com.scrappy.scrappy.controller.dto.project.*;
import com.scrappy.scrappy.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(ProjectCreateDTO createDTO, UserEntity owner) {
        ProjectEntity project = new ProjectEntity();
        project.setName(createDTO.getName());
        project.setDescription(createDTO.getDescription());
        project.setCategory(ProjectCategory.valueOf(createDTO.getCategory().toUpperCase()));
        project.setAddress(createDTO.getAddress());
        project.setOwner(owner);
        return project;
    }

    public ProjectDTO toDto(ProjectEntity project, ProjectRole currentUserRole) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory().getName());
        dto.setAddress(project.getAddress());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        dto.setOwnerTelegramId(project.getOwner().getTelegramId()); // Изменено на getTelegramId
        dto.setCurrentUserRole(currentUserRole != null ? currentUserRole.name().toLowerCase() : null);
        return dto;
    }

    public void updateEntity(ProjectEntity project, ProjectCreateDTO updateDTO) {
        project.setName(updateDTO.getName());
        project.setDescription(updateDTO.getDescription());
        project.setCategory(ProjectCategory.valueOf(updateDTO.getCategory().toUpperCase()));
        project.setAddress(updateDTO.getAddress());
    }

    public ProjectMemberEntity toMemberEntity(ProjectEntity project, UserEntity user, ProjectRole role, String position) {
        ProjectMemberEntity member = new ProjectMemberEntity();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        member.setPosition(position);
        return member;
    }

    public MemberDTO toMemberDto(ProjectMemberEntity member) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(member.getUser().getTelegramId()); // Изменено на getTelegramId
        dto.setTelegramUsername(member.getUser().getUsername());
        dto.setRole(member.getRole().name().toLowerCase());
        dto.setPosition(member.getPosition());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }

    public ProjectInviteEntity toInviteEntity(ProjectEntity project, InviteCreateDTO inviteDTO) {
        ProjectInviteEntity invite = new ProjectInviteEntity();
        invite.setProject(project);
        invite.setTelegramUsername(inviteDTO.getTelegramUsername());
        invite.setRole(ProjectRole.valueOf(inviteDTO.getRole().toUpperCase()));
        invite.setPosition(inviteDTO.getPosition());
        return invite;
    }

    public InviteDTO toInviteDto(ProjectInviteEntity invite) {
        InviteDTO dto = new InviteDTO();
        dto.setInviteId(invite.getInviteId());
        dto.setTelegramUsername(invite.getTelegramUsername());
        dto.setRole(invite.getRole().name().toLowerCase());
        dto.setPosition(invite.getPosition());
        dto.setInvitedAt(invite.getInvitedAt());
        dto.setExpiresAt(invite.getExpiresAt());
        return dto;
    }

    public ShiftEntity toShiftEntity(ProjectEntity project, UserEntity user, ShiftCreateDTO createDTO) {
        ShiftEntity shift = new ShiftEntity();
        shift.setProject(project);
        shift.setUser(user);
        shift.setDate(createDTO.getDate());
        shift.setStartTime(createDTO.getStartTime());
        shift.setEndTime(createDTO.getEndTime());
        return shift;
    }

    public ShiftDTO toShiftDto(ShiftEntity shift) {
        ShiftDTO dto = new ShiftDTO();
        dto.setId(shift.getId());
        dto.setProjectId(shift.getProject().getId());
        dto.setUserId(shift.getUser().getTelegramId());
        dto.setDate(shift.getDate());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        dto.setCreatedAt(shift.getCreatedAt());
        return dto;
    }

    public CalendarDTO.EmployeeShiftDTO toEmployeeShiftDto(UserEntity user, ProjectMemberEntity member, List<ShiftDTO> shifts) {
        CalendarDTO.EmployeeShiftDTO dto = new CalendarDTO.EmployeeShiftDTO();
        dto.setEmployeeId(user.getTelegramId().toString());
        dto.setName(user.getUsername());
        dto.setPosition(member.getPosition());
        dto.setRole(member.getRole().name().toLowerCase());
        dto.setShifts(shifts);
        return dto;
    }
}