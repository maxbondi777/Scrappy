package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.NoteCreateDTO;
import com.scrappy.scrappy.controller.dto.NoteDTO;
import com.scrappy.scrappy.controller.dto.NoteUpdateDTO;
import com.scrappy.scrappy.domain.Note;
import com.scrappy.scrappy.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NoteMapper {

    public NoteDTO toDto(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setUserId(note.getUser().getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCategory(note.getCategory());
        dto.setTags(note.getTags());
        dto.setPinned(note.isPinned());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }

    public Note toEntity(NoteCreateDTO createDTO, User user) {
        Note note = new Note();
        note.setUser(user);
        note.setTitle(createDTO.getTitle());
        note.setContent(createDTO.getContent());
        note.setCategory(createDTO.getCategory());
        note.setTags(createDTO.getTags());
        note.setPinned(false);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        return note;
    }

    public void updateEntity(Note note, NoteUpdateDTO updateDTO) {
        if (updateDTO.getTitle() != null) {
            note.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null) {
            note.setContent(updateDTO.getContent());
        }
        if (updateDTO.getCategory() != null) {
            note.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getTags() != null) {
            note.setTags(updateDTO.getTags());
        }
        note.setUpdatedAt(LocalDateTime.now());
    }
}