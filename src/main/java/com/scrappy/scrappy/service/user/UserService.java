package com.scrappy.scrappy.service.user;


import com.scrappy.scrappy.domain.Subscription;
import com.scrappy.scrappy.domain.UserEntity;
import com.scrappy.scrappy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.scrappy.scrappy.controller.dto.auth.TgUserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TgUserDTO getTgUser(Long telegramId) {
        logger.debug("Fetching user with Telegram ID: {}", telegramId);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + telegramId));
        return convertToTgUserDTO(user);
    }

    @Transactional
    public TgUserDTO createOrUpdateTgUser(TgUserDTO tgUserDTO) {
        TgUserDTO.User userData = tgUserDTO.getUser();
        String username = userData.getUsername();
        if (username == null || username.isBlank()) {
            username = "user_" + userData.getFirstName().toLowerCase().replaceAll("[^a-z0-9]", "_") + "_" + userData.getId();
            userData.setUsername(username);
            logger.debug("Generated username: {}", username);
        }
        logger.debug("Creating or updating user with username: {}", username);

        Optional<UserEntity> existingUserByTelegramId = userRepository.findByTelegramId(userData.getId());
        Optional<UserEntity> existingUserByUsername = userRepository.findByUsername(userData.getUsername());

        UserEntity user;
        if (existingUserByTelegramId.isPresent()) {
            user = existingUserByTelegramId.get();
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getTelegramId().equals(user.getTelegramId())) {
                logger.error("Username {} is already taken by another user", userData.getUsername());
                throw new IllegalArgumentException("Username is already taken: " + userData.getUsername());
            }
        } else if (existingUserByUsername.isPresent()) {
            logger.error("Username {} is already taken", userData.getUsername());
            throw new IllegalArgumentException("Username is already taken: " + userData.getUsername());
        } else {
            user = new UserEntity();
            user.setTelegramId(userData.getId());
        }

        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setLanguageCode(userData.getLanguageCode());
        user.setPhotoUrl(userData.getPhotoUrl());
        user.setAllowsWriteToPm(userData.getAllowsWriteToPm());
        user.setSubscription(tgUserDTO.getSubscription() != null ?
                Subscription.valueOf(tgUserDTO.getSubscription().name()) :
                Subscription.FREE);
        user.setHash(tgUserDTO.getHash());
        user.setQueryId(tgUserDTO.getQueryId());
        user.setSignature(tgUserDTO.getSignature());

        try {
            user = userRepository.save(user);
            logger.info("User saved successfully: {}", user.getUsername());
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while saving user: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to save user due to data integrity violation: " + e.getMessage());
        }

        return convertToTgUserDTO(user);
    }

    @Transactional
    public TgUserDTO updateSubscription(Long telegramId, Subscription subscription) {
        logger.debug("Updating subscription for Telegram ID: {} to {}", telegramId, subscription);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + telegramId));
        user.setSubscription(subscription);
        userRepository.save(user);
        return convertToTgUserDTO(user);
    }

    private TgUserDTO convertToTgUserDTO(UserEntity user) {
        TgUserDTO dto = new TgUserDTO();
        dto.setAuthDate(user.getCreatedAt());
        dto.setHash(user.getHash());
        dto.setQueryId(user.getQueryId());
        dto.setSignature(user.getSignature());
        dto.setUser(new TgUserDTO.User());
        dto.getUser().setAllowsWriteToPm(user.getAllowsWriteToPm());
        dto.getUser().setFirstName(user.getFirstName());
        dto.getUser().setId(user.getTelegramId());
        dto.getUser().setLastName(user.getLastName());
        dto.getUser().setLanguageCode(user.getLanguageCode());
        dto.getUser().setPhotoUrl(user.getPhotoUrl());
        dto.getUser().setUsername(user.getUsername());
        dto.setSubscription(Subscription.valueOf(user.getSubscription().name()));
        return dto;
    }
}