package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.note.NoteCreateDTO;
import com.scrappy.scrappy.controller.dto.note.NoteDTO;
import com.scrappy.scrappy.controller.dto.note.NoteUpdateDTO;
import com.scrappy.scrappy.controller.dto.note.NotesResponseDTO;
import com.scrappy.scrappy.repository.UserRepository;
import com.scrappy.scrappy.service.note.NoteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NoteDTO>> createNote(@Valid @RequestBody NoteCreateDTO createDTO,
                                                           @RequestHeader("X-User-Id") Long userId) {
        NoteDTO noteDTO = noteService.createNote(createDTO, userId);
        ApiResponse<NoteDTO> response = new ApiResponse<>(noteDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotesResponseDTO>> getNotes(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        NotesResponseDTO responseDTO = noteService.getNotes(userId, category, search, page, size);
        ApiResponse<NotesResponseDTO> response = new ApiResponse<>(responseDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NoteDTO>> getNoteById(@PathVariable Long id,
                                                            @RequestHeader("X-User-Id") Long userId) {
        NoteDTO noteDTO = noteService.getNoteById(id, userId);
        ApiResponse<NoteDTO> response = new ApiResponse<>(noteDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NoteDTO>> updateNote(@PathVariable Long id,
                                                           @Valid @RequestBody NoteUpdateDTO updateDTO,
                                                           @RequestHeader("X-User-Id") Long userId) {
        NoteDTO noteDTO = noteService.updateNote(id, updateDTO, userId);
        ApiResponse<NoteDTO> response = new ApiResponse<>(noteDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id,
                                                        @RequestHeader("X-User-Id") Long userId) {
        noteService.deleteNote(id, userId);
        ApiResponse<Void> response = new ApiResponse<>(null, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}/pin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NoteDTO>> togglePin(@PathVariable Long id,
                                                          @RequestHeader("X-User-Id") Long userId) {
        NoteDTO noteDTO = noteService.togglePin(id, userId);
        ApiResponse<NoteDTO> response = new ApiResponse<>(noteDTO, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = new ApiResponse<>(null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Void> response = new ApiResponse<>(null, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}