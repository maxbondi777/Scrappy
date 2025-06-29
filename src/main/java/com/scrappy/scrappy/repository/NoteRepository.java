package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByUserIdAndCategory(Long userId, String category);
    List<Note> findByUserIdAndTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(Long userId, String title, String tags);
}