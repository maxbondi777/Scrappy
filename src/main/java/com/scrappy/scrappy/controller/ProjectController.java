package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.project.*;
import com.scrappy.scrappy.service.project.MembersResponseDTO;
import com.scrappy.scrappy.service.project.ProjectService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"https://localhost:5173", "https://192.168.1.186:5173"}, allowCredentials = "true")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectCreateDTO createDTO,
                                                                 @RequestHeader("X-User-Id") Long userId) {
        ProjectDTO projectDTO = projectService.createProject(createDTO, userId);
        ApiResponse<ProjectDTO> response = new ApiResponse<>(projectDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects(@RequestHeader("X-User-Id") Long userId) {
        List<ProjectDTO> projects = projectService.getAllProjects(userId);
        ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(projects, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(@PathVariable UUID projectId,
                                                                 @Valid @RequestBody ProjectCreateDTO updateDTO,
                                                                 @RequestHeader("X-User-Id") Long userId) {
        ProjectDTO projectDTO = projectService.updateProject(projectId, updateDTO, userId);
        ApiResponse<ProjectDTO> response = new ApiResponse<>(projectDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID projectId,
                                                           @RequestHeader("X-User-Id") Long userId) {
        projectService.deleteProject(projectId, userId);
        ApiResponse<Void> response = new ApiResponse<>(null, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{projectId}/invites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InviteDTO>> createInvite(@PathVariable UUID projectId,
                                                               @Valid @RequestBody InviteCreateDTO inviteDTO,
                                                               @RequestHeader("X-User-Id") Long userId) {
        InviteDTO invite = projectService.createInvite(projectId, inviteDTO, userId);
        ApiResponse<InviteDTO> response = new ApiResponse<>(invite, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{projectId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MembersResponseDTO>> getMembers(@PathVariable UUID projectId,
                                                                      @RequestHeader("X-User-Id") Long userId) {
        MembersResponseDTO members = projectService.getMembers(projectId, userId);
        ApiResponse<MembersResponseDTO> response = new ApiResponse<>(members, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}/members", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable UUID projectId,
                                                          @RequestBody Map<String, String> request,
                                                          @RequestHeader("X-User-Id") Long userId) {
        String userIdToRemove = request.get("userId");
        projectService.removeMember(projectId, userIdToRemove, userId);
        ApiResponse<Void> response = new ApiResponse<>(null, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{projectId}/shifts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ShiftDTO>> createShift(@PathVariable UUID projectId,
                                                             @Valid @RequestBody ShiftCreateDTO createDTO,
                                                             @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received POST /api/projects/{}/shifts for userId: {}", projectId, userId);
        ShiftDTO shift = projectService.createShift(projectId, createDTO, userId);
        ApiResponse<ShiftDTO> response = new ApiResponse<>(shift, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{projectId}/shifts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CalendarDTO>> getShifts(@PathVariable UUID projectId,
                                                              @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam(value = "days", defaultValue = "7") int days,
                                                              @RequestHeader("X-User-Id") Long userId) {
        logger.debug("Received GET /api/projects/{}/shifts for userId: {}", projectId, userId);
        CalendarDTO calendar = projectService.getShifts(projectId, startDate, days, userId);
        ApiResponse<CalendarDTO> response = new ApiResponse<>(calendar, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>(null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        logger.error("Server error: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>(null, "Internal server error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}