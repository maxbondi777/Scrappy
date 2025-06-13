package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.CategoryStatsDTO;
import com.scrappy.scrappy.controller.dto.NoteCreateDTO;
import com.scrappy.scrappy.controller.dto.NoteDTO;
import com.scrappy.scrappy.controller.dto.NoteUpdateDTO;
import com.scrappy.scrappy.controller.dto.NotesResponseDTO;
import com.scrappy.scrappy.domain.CategoryType;
import com.scrappy.scrappy.domain.Note;
import com.scrappy.scrappy.domain.User;
import com.scrappy.scrappy.repository.NoteRepository;
import com.scrappy.scrappy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
    }

    @Transactional
    public NoteDTO createNote(NoteCreateDTO createDTO, Long userId) {
        logger.debug("Creating note for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Note note = noteMapper.toEntity(createDTO, user);
        Note savedNote = noteRepository.save(note);
        return noteMapper.toDto(savedNote);
    }

    @Transactional(readOnly = true)
    public NotesResponseDTO getNotes(Long userId, String category, String search, int page, int size) {
        logger.debug("Fetching notes for userId: {}, category: {}, search: {}, page: {}, size: {}", userId, category, search, page, size);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        CategoryType categoryType = category != null && !category.equalsIgnoreCase("all") ? CategoryType.valueOf(category.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("isPinned").descending().and(Sort.by("updatedAt").descending()));
        Page<Note> notesPage = noteRepository.findByUserAndFilters(user, categoryType, search, pageable);
        List<NoteDTO> noteDTOs = notesPage.getContent().stream()
                .map(noteMapper::toDto)
                .collect(Collectors.toList());
        List<CategoryStatsDTO> categoryStats = noteRepository.getCategoryStatsByUser(user);
        NotesResponseDTO response = new NotesResponseDTO();
        response.setNotes(noteDTOs);
        response.setTotalCount(notesPage.getTotalElements());
        response.setCategoriesStats(categoryStats);
        return response;
    }

    @Transactional(readOnly = true)
    public NoteDTO getNoteById(Long id, Long userId) {
        logger.debug("Fetching note with id: {} for userId: {}", id, userId);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        return noteMapper.toDto(note);
    }

    @Transactional
    public NoteDTO updateNote(Long id, NoteUpdateDTO updateDTO, Long userId) {
        logger.debug("Updating note with id: {} for userId: {}", id, userId);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        noteMapper.updateEntity(note, updateDTO);
        Note updatedNote = noteRepository.save(note);
        return noteMapper.toDto(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id, Long userId) {
        logger.debug("Deleting note with id: {} for userId: {}", id, userId);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        noteRepository.delete(note);
    }

    @Transactional
    public NoteDTO togglePin(Long id, Long userId) {
        logger.debug("Toggling pin for note with id: {} for userId: {}", id, userId);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        note.setPinned(!note.isPinned());
        note.setUpdatedAt(LocalDateTime.now());
        Note updatedNote = noteRepository.save(note);
        return noteMapper.toDto(updatedNote);
    }
}