package com.scrappy.scrappy.service.note;

import com.scrappy.scrappy.controller.dto.category.CategoryStatsDTO;
import com.scrappy.scrappy.controller.dto.note.NoteCreateDTO;
import com.scrappy.scrappy.controller.dto.note.NoteDTO;
import com.scrappy.scrappy.controller.dto.note.NoteUpdateDTO;
import com.scrappy.scrappy.controller.dto.note.NotesResponseDTO;
import com.scrappy.scrappy.domain.CategoryType;
import com.scrappy.scrappy.domain.NoteEntity;
import com.scrappy.scrappy.domain.UserEntity;
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
    public NoteDTO createNote(NoteCreateDTO createDTO, Long telegramId) {
        logger.debug("Creating note for telegramId: {}", telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        NoteEntity note = noteMapper.toEntity(createDTO, user);
        NoteEntity savedNote = noteRepository.save(note);
        return noteMapper.toDto(savedNote);
    }

    @Transactional(readOnly = true)
    public NotesResponseDTO getNotes(Long telegramId, String category, String search, int page, int size) {
        logger.debug("Fetching notes for telegramId: {}, category: {}, search: {}, page: {}, size: {}", telegramId, category, search, page, size);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        CategoryType categoryType = category != null && !category.equalsIgnoreCase("all") ? CategoryType.valueOf(category.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("isPinned").descending().and(Sort.by("updatedAt").descending()));
        Page<NoteEntity> notesPage = noteRepository.findByUserAndFilters(user, categoryType, search, pageable);
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
    public NoteDTO getNoteById(Long id, Long telegramId) {
        logger.debug("Fetching note with id: {} for telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        return noteMapper.toDto(note);
    }

    @Transactional
    public NoteDTO updateNote(Long id, NoteUpdateDTO updateDTO, Long telegramId) {
        logger.debug("Updating note with id: {} for telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        noteMapper.updateEntity(note, updateDTO);
        NoteEntity updatedNote = noteRepository.save(note);
        return noteMapper.toDto(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id, Long telegramId) {
        logger.debug("Deleting note with id: {} for telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        noteRepository.delete(note);
    }

    @Transactional
    public NoteDTO togglePin(Long id, Long telegramId) {
        logger.debug("Toggling pin for note with id: {} for telegramId: {}", id, telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));
        NoteEntity note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: Note does not belong to user");
        }
        note.setPinned(!note.isPinned());
        note.setUpdatedAt(LocalDateTime.now());
        NoteEntity updatedNote = noteRepository.save(note);
        return noteMapper.toDto(updatedNote);
    }
}