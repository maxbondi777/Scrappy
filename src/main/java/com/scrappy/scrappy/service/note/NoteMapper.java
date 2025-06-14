package com.scrappy.scrappy.service.note;

import com.scrappy.scrappy.controller.dto.note.NoteCreateDTO;
import com.scrappy.scrappy.controller.dto.note.NoteDTO;
import com.scrappy.scrappy.controller.dto.note.NoteUpdateDTO;
import com.scrappy.scrappy.domain.NoteEntity;
import com.scrappy.scrappy.domain.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NoteMapper {

    public NoteDTO toDto(NoteEntity note) {
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

    public NoteEntity toEntity(NoteCreateDTO createDTO, UserEntity user) {
        NoteEntity note = new NoteEntity();
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

    public void updateEntity(NoteEntity note, NoteUpdateDTO updateDTO) {
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