package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.Project;
import com.scrappy.scrappy.entity.ProjectInvite;
import com.scrappy.scrappy.entity.ProjectMember;
import com.scrappy.scrappy.entity.Shift;

import com.scrappy.scrappy.repository.ProjectInviteRepository;
import com.scrappy.scrappy.repository.ProjectMemberRepository;
import com.scrappy.scrappy.repository.ProjectRepository;
import com.scrappy.scrappy.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private ProjectInviteRepository projectInviteRepository;
    @Autowired
    private ShiftRepository shiftRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(@RequestHeader("X-User-Id") UUID userId, @RequestBody Project project) {
        project.setOwnerId(userId);
        project.setCreatedAt(new Date());
        project.setUpdatedAt(new Date());
        Project savedProject = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setProjectId(savedProject.getId());
        member.setUserId(userId);
        member.setRole("admin");
        member.setJoinedAt(new Date());
        projectMemberRepository.save(member);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("project", savedProject);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Project project) {
        return projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .map(p -> {
                    p.setName(project.getName());
                    p.setDescription(project.getDescription());
                    p.setCategory(project.getCategory());
                    p.setAddress(project.getAddress());
                    p.setUpdatedAt(new Date());
                    return ResponseEntity.ok(projectRepository.save(p));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId) {
        return projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .map(p -> {
                    projectRepository.delete(p);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{projectId}/invites")
    public ResponseEntity<Map<String, Object>> inviteMember(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, String> invite) {
        return projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .map(p -> {
                    ProjectInvite projectInvite = new ProjectInvite();
                    projectInvite.setProjectId(projectId);
                    projectInvite.setTelegramUsername(invite.get("telegramUsername"));
                    projectInvite.setRole(invite.get("role"));
                    projectInvite.setPosition(invite.get("position"));
                    ProjectInvite savedInvite = projectInviteRepository.save(projectInvite);
                    Map<String, Object> response = new HashMap<>();
                    response.put("inviteId", savedInvite.getInviteId());
                    response.put("expiresAt", savedInvite.getExpiresAt());
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<Map<String, Object>> getMembers(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId) {
        return projectRepository.findById(projectId)
                .map(p -> {
                    List<ProjectMember> activeMembers = projectMemberRepository.findByProjectId(projectId);
                    List<ProjectInvite> pendingInvites = projectInviteRepository.findByProjectId(projectId);
                    Map<String, Object> response = new HashMap<>();
                    response.put("activeMembers", activeMembers);
                    response.put("pendingInvites", pendingInvites);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectId}/members")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, UUID> request) {
        return projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .map(p -> {
                    projectMemberRepository.deleteById(request.get("userId"));
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{projectId}/shifts")
    public ResponseEntity<Map<String, Object>> addShift(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, Object> shiftData) {
        return projectRepository.findById(projectId)
                .filter(p -> p.getOwnerId().equals(userId))
                .map(p -> {
                    Shift shift = new Shift();
                    shift.setProjectId(projectId);
                    shift.setUserId(UUID.fromString(shiftData.get("userId").toString()));
                    shift.setDate(new Date(shiftData.get("date").toString()));
                    shift.setStartTime(shiftData.get("startTime").toString());
                    shift.setEndTime(shiftData.get("endTime").toString());
                    Shift savedShift = shiftRepository.save(shift);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("shift", savedShift);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{projectId}/shifts")
    public ResponseEntity<Map<String, Object>> getShifts(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId,
                                                         @RequestParam String start_date, @RequestParam int days) {
        return projectRepository.findById(projectId)
                .map(p -> {
                    List<Shift> shifts = shiftRepository.findAll();
                    Map<String, Object> calendar = new HashMap<>();
                    calendar.put("date_range", Map.of("start", start_date, "end", start_date));
                    calendar.put("shifts", shifts);
                    Map<String, Object> response = new HashMap<>();
                    response.put("calendar", calendar);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }
}