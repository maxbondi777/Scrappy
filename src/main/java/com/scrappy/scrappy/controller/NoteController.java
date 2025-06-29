package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.Note;
import com.scrappy.scrappy.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class NoteController {
    @Autowired
    private NoteRepository noteRepository;

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestHeader("X-User-Id") Long userId, @RequestBody Note note) {
        note.setUserId(userId);
        note.setCreatedAt(new Date());
        note.setUpdatedAt(new Date());
        return ResponseEntity.ok(noteRepository.save(note));
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(noteRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return noteRepository.findById(id)
                .filter(note -> note.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId, @RequestBody Note note) {
        return noteRepository.findById(id)
                .filter(n -> n.getUserId().equals(userId))
                .map(n -> {
                    n.setTitle(note.getTitle());
                    n.setContent(note.getContent());
                    n.setCategory(note.getCategory());
                    n.setTags(note.getTags());
                    n.setPinned(note.isPinned());
                    n.setUpdatedAt(new Date());
                    return ResponseEntity.ok(noteRepository.save(n));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return noteRepository.findById(id)
                .filter(note -> note.getUserId().equals(userId))
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<Note> pinNote(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return noteRepository.findById(id)
                .filter(note -> note.getUserId().equals(userId))
                .map(note -> {
                    note.setPinned(!note.isPinned());
                    note.setUpdatedAt(new Date());
                    return ResponseEntity.ok(noteRepository.save(note));
                }).orElse(ResponseEntity.notFound().build());
    }
}