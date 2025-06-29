package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.TgUser;
import com.scrappy.scrappy.repository.TgUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class AuthController {
    @Autowired
    private TgUserRepository tgUserRepository;

    @GetMapping("/tguser")
    public ResponseEntity<TgUser> getTgUser(@RequestHeader("X-User-Id") Long telegramId) {
        return tgUserRepository.findByTelegramId(telegramId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tguser")
    public ResponseEntity<TgUser> createTgUser(@RequestBody TgUser tgUser) {
        tgUser.setAuthDate(new Date());
        return ResponseEntity.ok(tgUserRepository.save(tgUser));
    }
}