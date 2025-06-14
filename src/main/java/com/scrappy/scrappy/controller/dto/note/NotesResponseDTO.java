package com.scrappy.scrappy.controller.dto.note;

import com.scrappy.scrappy.controller.dto.category.CategoryStatsDTO;
import lombok.Data;

import java.util.List;

@Data
public class NotesResponseDTO {
    private List<NoteDTO> notes;
    private long totalCount;
    private List<CategoryStatsDTO> categoriesStats;
}