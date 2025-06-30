package com.scrappy.scrappy.controller;


import com.scrappy.scrappy.entity.Subscription;
import com.scrappy.scrappy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/tguser")
    public ResponseEntity<ApiResponse<TgUserDTO>> getTgUser(@RequestHeader("X-User-Id") Long telegramId) {
        TgUserDTO tgUser = userService.getTgUser(telegramId);
        return ResponseEntity.ok(new ApiResponse<>(tgUser, null));
    }

    @PostMapping("/tguser")
    public ResponseEntity<ApiResponse<TgUserDTO>> createOrUpdateTgUser(@Valid @RequestBody TgUserDTO tgUserDTO) {
        logger.debug("Received POST /auth/tguser with TgUserDTO: {}", tgUserDTO);
        TgUserDTO updatedTgUser = userService.createOrUpdateTgUser(tgUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(updatedTgUser, null));
    }

    @PostMapping("/user/subscription")
    public ResponseEntity<ApiResponse<TgUserDTO>> updateSubscription(@RequestHeader("X-User-Id") Long telegramId,
                                                                     @RequestParam String subscription) {
        try {
            Subscription sub = Subscription.valueOf(subscription.toUpperCase());
            TgUserDTO tgUser = userService.updateSubscription(telegramId, sub);
            return ResponseEntity.ok(new ApiResponse<>(tgUser, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, "Invalid subscription value: " + subscription));
        }
    }
}