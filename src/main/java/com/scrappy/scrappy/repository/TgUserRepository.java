package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {
    Optional<TgUser> findByTelegramId(Long telegramId);
}