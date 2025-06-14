package com.scrappy.scrappy.service.project;

import com.scrappy.scrappy.controller.dto.project.*;
import com.scrappy.scrappy.domain.*;
import com.scrappy.scrappy.repository.*;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectInviteRepository projectInviteRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
                          ProjectInviteRepository projectInviteRepository, ShiftRepository shiftRepository,
                          UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectInviteRepository = projectInviteRepository;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectDTO createProject(ProjectCreateDTO createDTO, Long userId) {
        logger.debug("Creating project for userId: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        ProjectEntity project = projectMapper.toEntity(createDTO, user);
        ProjectEntity savedProject = projectRepository.save(project);

        // Add owner as admin
        ProjectMemberEntity member = projectMapper.toMemberEntity(savedProject, user, ProjectRole.ADMIN, "Owner");
        projectMemberRepository.save(member);

        return projectMapper.toDto(savedProject, ProjectRole.ADMIN);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects(Long userId) {
        logger.debug("Fetching projects for userId: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        List<ProjectEntity> ownedProjects = projectRepository.findByOwner(user);
        List<ProjectMemberEntity> memberships = projectMemberRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        List<ProjectEntity> memberProjects = memberships.stream()
                .map(ProjectMemberEntity::getProject)
                .collect(Collectors.toList());

        return ownedProjects.stream()
                .map(p -> projectMapper.toDto(p, ProjectRole.ADMIN))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO updateProject(UUID projectId, ProjectCreateDTO updateDTO, Long userId) {
        logger.debug("Updating project {} for userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkAdminAccess(project, userId);
        projectMapper.updateEntity(project, updateDTO);
        ProjectEntity updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject, getUserRole(project, userId));
    }

    @Transactional
    public void deleteProject(UUID projectId, Long userId) {
        logger.debug("Deleting project {} for userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkAdminAccess(project, userId);
        projectRepository.delete(project);
    }

    @Transactional
    public InviteDTO createInvite(UUID projectId, InviteCreateDTO inviteDTO, Long userId) {
        logger.debug("Creating invite for project {} by userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkAdminAccess(project, userId);
        ProjectInviteEntity invite = projectMapper.toInviteEntity(project, inviteDTO);
        ProjectInviteEntity savedInvite = projectInviteRepository.save(invite);
        // TODO: Send Telegram notification
        return projectMapper.toInviteDto(savedInvite);
    }

    @Transactional(readOnly = true)
    public MembersResponseDTO getMembers(UUID projectId, Long userId) {
        logger.debug("Fetching members for project {} by userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkMemberAccess(project, userId);
        List<MemberDTO> activeMembers = projectMemberRepository.findByProject(project).stream()
                .map(projectMapper::toMemberDto)
                .collect(Collectors.toList());
        List<InviteDTO> pendingInvites = projectInviteRepository.findByProject(project).stream()
                .map(projectMapper::toInviteDto)
                .collect(Collectors.toList());
        MembersResponseDTO response = new MembersResponseDTO();
        response.setActiveMembers(activeMembers);
        response.setPendingInvites(pendingInvites);
        return response;
    }

    @Transactional
    public void removeMember(UUID projectId, String userIdToRemove, Long userId) {
        logger.debug("Removing member {} from project {} by userId: {}", userIdToRemove, projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkAdminAccess(project, userId);
        Long userIdLong = Long.parseLong(userIdToRemove); // Изменено с UUID на Long
        UserEntity userToRemove = userRepository.findById(userIdLong)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userIdToRemove));
        ProjectMemberEntity member = projectMemberRepository.findByProjectAndUser(project, userToRemove)
                .orElseThrow(() -> new IllegalArgumentException("Member not found in project"));
        projectMemberRepository.delete(member);
    }

    @Transactional
    public ShiftDTO createShift(UUID projectId, ShiftCreateDTO createDTO, Long userId) {
        logger.debug("Creating shift for project {} by userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkAdminAccess(project, userId);
        Long userIdLong = Long.parseLong(createDTO.getUserId()); // Изменено с UUID на Long
        UserEntity user = userRepository.findById(userIdLong)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + createDTO.getUserId()));
        checkMemberAccess(project, user.getId());
        ShiftEntity shift = projectMapper.toShiftEntity(project, user, createDTO);
        ShiftEntity savedShift = shiftRepository.save(shift);
        // TODO: Send Telegram notification
        return projectMapper.toShiftDto(savedShift);
    }

    @Transactional(readOnly = true)
    public CalendarDTO getShifts(UUID projectId, LocalDate startDate, int days, Long userId) {
        logger.debug("Fetching shifts for project {} by userId: {}", projectId, userId);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        checkMemberAccess(project, userId);
        LocalDate endDate = startDate.plusDays(days - 1);
        List<ShiftEntity> shifts = shiftRepository.findByProjectAndDateBetween(project, startDate, endDate);
        List<ProjectMemberEntity> members = projectMemberRepository.findByProject(project);

        CalendarDTO calendar = new CalendarDTO();
        CalendarDTO.DateRange dateRange = new CalendarDTO.DateRange();
        dateRange.setStart(startDate);
        dateRange.setEnd(endDate);
        calendar.setDateRange(dateRange);

        List<CalendarDTO.EmployeeShiftDTO> employeeShifts = members.stream()
                .map(member -> {
                    List<ShiftDTO> memberShifts = shifts.stream()
                            .filter(s -> s.getUser().getId().equals(member.getUser().getId()))
                            .map(projectMapper::toShiftDto)
                            .collect(Collectors.toList());
                    return projectMapper.toEmployeeShiftDto(member.getUser(), member, memberShifts);
                })
                .filter(dto -> !dto.getShifts().isEmpty())
                .collect(Collectors.toList());

        calendar.setShifts(employeeShifts);
        return calendar;
    }

    private void checkAdminAccess(ProjectEntity project, Long userId) {
        ProjectMemberEntity member = projectMemberRepository.findByProjectAndUser(project, userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId)))
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the project"));
        if (member.getRole() != ProjectRole.ADMIN) {
            throw new IllegalArgumentException("Only admins can perform this action");
        }
    }

    private void checkMemberAccess(ProjectEntity project, Long userId) {
        projectMemberRepository.findByProjectAndUser(project, userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId)))
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the project"));
    }

    private ProjectRole getUserRole(ProjectEntity project, Long userId) {
        return projectMemberRepository.findByProjectAndUser(project, userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId)))
                .map(ProjectMemberEntity::getRole)
                .orElse(null);
    }
}
