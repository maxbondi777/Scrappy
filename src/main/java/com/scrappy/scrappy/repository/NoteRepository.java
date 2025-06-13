package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.controller.dto.CategoryStatsDTO;
import com.scrappy.scrappy.domain.CategoryType;
import com.scrappy.scrappy.domain.Note;
import com.scrappy.scrappy.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n FROM Note n WHERE n.user = :user AND (:category IS NULL OR n.category = :category) AND " +
            "(:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(n.tags AS string)) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Note> findByUserAndFilters(@Param("user") User user, @Param("category") CategoryType category,
                                    @Param("search") String search, Pageable pageable);

    @Query("SELECT new com.scrappy.scrappy.controller.dto.CategoryStatsDTO(n.category, COUNT(n)) " +
            "FROM Note n WHERE n.user = :user GROUP BY n.category")
    List<CategoryStatsDTO> getCategoryStatsByUser(@Param("user") User user);
}