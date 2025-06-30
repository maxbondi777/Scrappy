package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.*;

import com.scrappy.scrappy.repository.ProjectInviteRepository;
import com.scrappy.scrappy.repository.ProjectMemberRepository;
import com.scrappy.scrappy.repository.ProjectRepository;
import com.scrappy.scrappy.repository.ShiftRepository;
import com.scrappy.scrappy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createProject(@RequestHeader("X-User-Id") Long userId, @RequestBody Project project) {
        try {
            User user = userService.getUser(userId.toString()); // Предполагаем, что getUser принимает String (telegramId или id)
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            if (project.getName() == null || project.getName().length() < 3) {
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Имя должно содержать минимум 3 символа"));
            }
            if (project.getDescription() == null || project.getDescription().length() < 10) {
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Описание должно содержать минимум 10 символов"));
            }
            if (!isValidCategory(project.getCategory())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Недопустимая категория"));
            }

            project.setOwner(user);
            project.setCreatedAt(LocalDateTime.now(ZoneId.of("Europe/Berlin")));
            project.setUpdatedAt(LocalDateTime.now(ZoneId.of("Europe/Berlin")));
            Project savedProject = projectRepository.save(project);

            ProjectMember member = new ProjectMember();
            member.setProject(savedProject);
            member.setUser(user);
            member.setRole("admin");
            member.setJoinedAt(LocalDateTime.now(ZoneId.of("Europe/Berlin")));
            projectMemberRepository.save(member);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("project", Map.of(
                    "id", savedProject.getId(),
                    "name", savedProject.getName(),
                    "description", savedProject.getDescription(),
                    "category", savedProject.getCategory(),
                    "address", savedProject.getAddress(),
                    "createdAt", savedProject.getCreatedAt(),
                    "updatedAt", savedProject.getUpdatedAt(),
                    "ownerId", savedProject.getOwner().getId(),
                    "currentUserRole", "admin"
            ));
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects(@RequestHeader("X-User-Id") Long userId) {
        try {
            User user = userService.getUser(userId.toString()); // Предполагаем, что getUser принимает String (id или telegramId)
            if (user == null) {
                return ResponseEntity.status(401).body(null);
            }
            return ResponseEntity.ok(projectRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Или верни Map с ошибкой, если нужно
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Project project) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .filter(p -> p.getOwner().getId().equals(user.getId()))
                    .map(p -> {
                        if (project.getName() != null && project.getName().length() < 3) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Имя должно содержать минимум 3 символа"));
                        }
                        if (project.getDescription() != null && project.getDescription().length() < 10) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Описание должно содержать минимум 10 символов"));
                        }
                        if (project.getCategory() != null && !isValidCategory(project.getCategory())) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "Недопустимая категория"));
                        }

                        p.setName(project.getName());
                        p.setDescription(project.getDescription());
                        p.setCategory(project.getCategory());
                        p.setAddress(project.getAddress());
                        p.setUpdatedAt(LocalDateTime.now(ZoneId.of("Europe/Berlin")));
                        return ResponseEntity.ok(projectRepository.save(p));
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId) {
        try {
            // Проверяем существование пользователя
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }

            // Ищем проект и проверяем права доступа
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            if (projectOptional.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует"));
            }

            Project project = projectOptional.get();
            if (!project.getOwner().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Доступ запрещен", "details", "У вас нет прав на удаление этого проекта"));
            }

            // Удаляем проект
            projectRepository.delete(project);
            return ResponseEntity.ok().<Void>build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @PostMapping("/{projectId}/invites")
    public ResponseEntity<?> inviteMember(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, String> invite) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .filter(p -> p.getOwner().getId().equals(user.getId()))
                    .map(p -> {
                        ProjectInvite projectInvite = new ProjectInvite();
                        projectInvite.setProject(p);
                        projectInvite.setTelegramUsername(invite.get("telegramUsername"));
                        projectInvite.setRole(invite.get("role"));
                        projectInvite.setPosition(invite.get("position"));
                        ProjectInvite savedInvite = projectInviteRepository.save(projectInvite);
                        Map<String, Object> response = new HashMap<>();
                        response.put("inviteId", savedInvite.getId());
                        response.put("expiresAt", savedInvite.getExpiresAt());
                        return ResponseEntity.ok(response);
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<?> getMembers(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .map(p -> {
                        List<ProjectMember> activeMembers = projectMemberRepository.findByProjectId(projectId);
                        List<ProjectInvite> pendingInvites = projectInviteRepository.findByProjectId(projectId);
                        Map<String, Object> response = new HashMap<>();
                        response.put("activeMembers", activeMembers);
                        response.put("pendingInvites", pendingInvites);
                        return ResponseEntity.ok(response);
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}/members")
    public ResponseEntity<?> deleteMember(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, Long> request) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .filter(p -> p.getOwner().getId().equals(user.getId()))
                    .map(p -> {
                        Long userIdToDelete = request.get("userId");
                        if (userIdToDelete != null) {
                            Optional<ProjectMember> member = projectMemberRepository.findByUserId(userIdToDelete);
                            member.ifPresent(projectMemberRepository::delete);
                            return ResponseEntity.ok().<Void>build();
                        }
                        return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные", "details", "userId не указан"));
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @PostMapping("/{projectId}/shifts")
    public ResponseEntity<?> addShift(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId, @RequestBody Map<String, Object> shiftData) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .filter(p -> p.getOwner().getId().equals(user.getId()))
                    .map(p -> {
                        Shift shift = new Shift();
                        shift.setProject(p);
                        shift.setUser(user);
                        shift.setDate(LocalDateTime.parse(shiftData.get("date").toString() + "T00:00:00"));
                        shift.setStartTime(shiftData.get("startTime").toString());
                        shift.setEndTime(shiftData.get("endTime").toString());
                        Shift savedShift = shiftRepository.save(shift);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("shift", Map.of(
                                "id", savedShift.getId(),
                                "projectId", savedShift.getProject().getId(),
                                "userId", savedShift.getUser().getId(),
                                "date", savedShift.getDate().toLocalDate(),
                                "startTime", savedShift.getStartTime(),
                                "endTime", savedShift.getEndTime(),
                                "createdAt", savedShift.getCreatedAt()
                        ));
                        return ResponseEntity.ok(response);
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    @GetMapping("/{projectId}/shifts")
    public ResponseEntity<?> getShifts(@PathVariable UUID projectId, @RequestHeader("X-User-Id") UUID userId,
                                       @RequestParam String start_date, @RequestParam int days) {
        try {
            User user = userService.getUser(userId.toString());
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован", "details", "Пользователь не найден"));
            }
            return projectRepository.findById(projectId)
                    .map(p -> {
                        LocalDate start = LocalDate.parse(start_date);
                        LocalDateTime startDateTime = start.atStartOfDay();
                        LocalDateTime endDateTime = start.plusDays(days - 1).atTime(23, 59, 59);
                        List<Shift> shifts = shiftRepository.findByProjectIdAndDateBetween(projectId, startDateTime, endDateTime);
                        Map<String, Object> calendar = new HashMap<>();
                        calendar.put("date_range", Map.of("start", start, "end", start.plusDays(days - 1)));
                        calendar.put("shifts", shifts.stream().map(shift -> Map.of(
                                "employee_id", shift.getUser().getId().toString(),
                                "name", (shift.getUser().getFirstName() != null ? shift.getUser().getFirstName() : "") + " " + (shift.getUser().getLastName() != null ? shift.getUser().getLastName() : ""),
                                "position", getPosition(shift.getUser().getId(), projectId),
                                "role", getRole(shift.getUser().getId(), projectId),
                                "shifts", List.of(Map.of(
                                        "id", shift.getId(),
                                        "date", shift.getDate().toLocalDate(),
                                        "startTime", shift.getStartTime(),
                                        "endTime", shift.getEndTime()
                                ))
                        )).toList());
                        Map<String, Object> response = new HashMap<>();
                        response.put("calendar", calendar);
                        return ResponseEntity.ok(response);
                    }).orElse(ResponseEntity.status(404).body(Map.of("error", "Проект не найден", "details", "Проект с ID " + projectId + " не существует")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера", "details", e.getMessage()));
        }
    }

    private boolean isValidCategory(String category) {
        String[] validCategories = {
                "Офисная деятельность", "Ресторанная деятельность", "Личный проект", "Логистика",
                "Планер задач", "Личный ассистент", "Образование", "Творческие проекты", "Здравоохранение"
        };
        return Arrays.stream(validCategories).anyMatch(c -> c != null && c.equals(category));
    }

    private String getRole(Long userId, UUID projectId) {
        Optional<ProjectMember> member = projectMemberRepository.findByUserId(userId);
        return member.filter(m -> m.getProject().getId().equals(projectId))
                .map(ProjectMember::getRole)
                .orElse("employee");
    }

    private String getPosition(Long userId, UUID projectId) {
        Optional<ProjectMember> member = projectMemberRepository.findByUserId(userId);
        return member.filter(m -> m.getProject().getId().equals(projectId))
                .map(ProjectMember::getPosition)
                .orElse(null);
    }
}