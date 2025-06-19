package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.auth.TgUserDTO;
import com.scrappy.scrappy.domain.Subscription;
import com.scrappy.scrappy.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/tguser")
    public ResponseEntity<ApiResponse<TgUserDTO>> getTgUser(@RequestHeader("X-User-Id") Long telegramId) {
        TgUserDTO tgUser = userService.getTgUser(telegramId);
        return new ResponseEntity<>(new ApiResponse<>(tgUser, null), HttpStatus.OK);
    }

    @PostMapping("/tguser")
    public ResponseEntity<ApiResponse<TgUserDTO>> createOrUpdateTgUser(@Valid @RequestBody TgUserDTO tgUserDTO,
                                                                       @RequestHeader("X-User-Id") Long telegramId) {
        logger.debug("Received POST /auth/tguser with TgUserDTO: {}, telegramId: {}", tgUserDTO, telegramId);
        if (tgUserDTO.getAuthDate() == null) {
            tgUserDTO.setAuthDate(LocalDateTime.now());
        }
        tgUserDTO.getUser().setId(telegramId); // Устанавливаем telegramId
        TgUserDTO updatedTgUser = userService.createOrUpdateTgUser(tgUserDTO);
        return new ResponseEntity<>(new ApiResponse<>(updatedTgUser, null), HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<TgUserDTO>> getUser(@RequestHeader("X-User-Id") Long telegramId) {
        TgUserDTO tgUser = userService.getTgUser(telegramId);
        return new ResponseEntity<>(new ApiResponse<>(tgUser, null), HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<TgUserDTO>> createOrUpdateUser(@RequestBody TgUserDTO tgUserDTO,
                                                                     @RequestHeader("X-User-Id") Long telegramId) {
        tgUserDTO.getUser().setId(telegramId); // Устанавливаем telegramId
        TgUserDTO updatedTgUser = userService.createOrUpdateTgUser(tgUserDTO);
        return new ResponseEntity<>(new ApiResponse<>(updatedTgUser, null), HttpStatus.CREATED);
    }

    @PostMapping("/user/subscription")
    public ResponseEntity<ApiResponse<TgUserDTO>> updateSubscription(@RequestHeader("X-User-Id") Long telegramId,
                                                                     @RequestParam String subscription) {
        try {
            Subscription sub = Subscription.valueOf(subscription.toUpperCase());
            TgUserDTO tgUser = userService.updateSubscription(telegramId, sub);
            return new ResponseEntity<>(new ApiResponse<>(tgUser, null), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(null, "Invalid subscription value: " + subscription), HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(new ApiResponse<>(null, "Validation failed: " + errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse<>(null, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse<>(null, "Internal server error: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}