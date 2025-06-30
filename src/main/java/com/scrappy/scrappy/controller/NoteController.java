package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.Note;
import com.scrappy.scrappy.entity.User;
import com.scrappy.scrappy.repository.NoteRepository;
import com.scrappy.scrappy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class NoteController {
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestHeader("X-User-Id") Long telegramId, @RequestBody NoteDTO noteDTO) {
        User user = userService.getUser(String.valueOf(telegramId));
        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setCategory(noteDTO.getCategory());
        note.setTags(noteDTO.getTags());
        note.setPinned(noteDTO.isPinned());
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        Note savedNote = noteRepository.save(note);

        // Преобразование в NoteDTO с добавлением telegramId
        NoteDTO responseDTO = new NoteDTO();
        responseDTO.setId(savedNote.getId());
        responseDTO.setTelegramId(user.getTelegramId()); // Устанавливаем telegramId пользователя
        responseDTO.setTitle(savedNote.getTitle());
        responseDTO.setContent(savedNote.getContent());
        responseDTO.setCategory(savedNote.getCategory());
        responseDTO.setTags(savedNote.getTags());
        responseDTO.setCreatedAt(savedNote.getCreatedAt());
        responseDTO.setUpdatedAt(savedNote.getUpdatedAt());
        responseDTO.setPinned(savedNote.isPinned());

        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return ResponseEntity.ok(noteRepository.findByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId, @RequestBody Note note) {
        User user = userService.getUser(String.valueOf(telegramId));
        return noteRepository.findById(id)
                .filter(n -> n.getUser().getId().equals(user.getId()))
                .map(n -> {
                    n.setTitle(note.getTitle());
                    n.setContent(note.getContent());
                    n.setCategory(note.getCategory());
                    n.setTags(note.getTags());
                    n.setPinned(note.isPinned());
                    return ResponseEntity.ok(noteRepository.save(n));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(user.getId()))
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<Note> pinNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long telegramId) {
        User user = userService.getUser(String.valueOf(telegramId));
        return noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(user.getId()))
                .map(note -> {
                    note.setPinned(!note.isPinned());
                    return ResponseEntity.ok(noteRepository.save(note));
                }).orElse(ResponseEntity.notFound().build());
    }
}