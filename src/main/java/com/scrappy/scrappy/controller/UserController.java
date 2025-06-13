package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.UserCreateDTO;
import com.scrappy.scrappy.controller.dto.UserDTO;
import com.scrappy.scrappy.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        logger.debug("Received POST /api/users for username: {}", createDTO.getUsername());
        UserDTO userDTO = userService.createUser(createDTO);
        ApiResponse<UserDTO> response = new ApiResponse<>(userDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>(null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Void> response = new ApiResponse<>(null, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}