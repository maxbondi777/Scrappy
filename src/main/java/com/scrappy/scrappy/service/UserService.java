package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.TgUserDTO;
import com.scrappy.scrappy.entity.Subscription;
import com.scrappy.scrappy.entity.User;
import com.scrappy.scrappy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TgUserDTO getTgUser(Long telegramId) {
        logger.debug("Fetching user with Telegram ID: {}", telegramId);
        User user = userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> createDefaultUser(telegramId));
        return convertToTgUserDTO(user);
    }

    public User getUser(String userIdStr) {
        logger.debug("Fetching user entity with user ID string: {}", userIdStr);
        try {
            Long telegramId = Long.parseLong(userIdStr); // Попытка преобразовать UUID в Long
            return userRepository.findByTelegramId(telegramId)
                    .orElseGet(() -> createDefaultUser(telegramId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + userIdStr);
        }
    }

    private User createDefaultUser(Long telegramId) {
        User user = new User();
        user.setTelegramId(telegramId);
        user.setUsername("user_" + telegramId);
        user.setFirstName("User_" + telegramId);
        return userRepository.save(user);
    }

    public TgUserDTO createOrUpdateTgUser(TgUserDTO tgUserDTO) {
        Long telegramId = tgUserDTO.getUser().getId();
        logger.debug("Creating or updating user with Telegram ID: {}", telegramId);

        User user = userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> new User());

        user.setTelegramId(telegramId);
        user.setUsername(tgUserDTO.getUser().getUsername() != null ? tgUserDTO.getUser().getUsername() : "user_" + telegramId);
        user.setFirstName(tgUserDTO.getUser().getFirstName());
        user.setLastName(tgUserDTO.getUser().getLastName());
        user.setLanguageCode(tgUserDTO.getUser().getLanguageCode());
        user.setPhotoUrl(tgUserDTO.getUser().getPhotoUrl());
        user.setAllowsWriteToPm(tgUserDTO.getUser().getAllowsWriteToPm());
        user.setSubscription(tgUserDTO.getSubscription() != null ? tgUserDTO.getSubscription() : Subscription.FREE);
        user.setHash(tgUserDTO.getHash());
        user.setQueryId(tgUserDTO.getQueryId());
        user.setSignature(tgUserDTO.getSignature());

        user = userRepository.save(user);
        return convertToTgUserDTO(user);
    }

    public TgUserDTO updateSubscription(Long telegramId, Subscription subscription) {
        logger.debug("Updating subscription for Telegram ID: {} to {}", telegramId, subscription);
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + telegramId));
        user.setSubscription(subscription);
        userRepository.save(user);
        return convertToTgUserDTO(user);
    }

    private TgUserDTO convertToTgUserDTO(User user) {
        TgUserDTO dto = new TgUserDTO();
        dto.setAuthDate(user.getCreatedAt());
        dto.setHash(user.getHash());
        dto.setQueryId(user.getQueryId());
        dto.setSignature(user.getSignature());
        TgUserDTO.User userDto = new TgUserDTO.User();
        userDto.setAllowsWriteToPm(user.getAllowsWriteToPm());
        userDto.setFirstName(user.getFirstName());
        userDto.setId(user.getTelegramId());
        userDto.setLastName(user.getLastName());
        userDto.setLanguageCode(user.getLanguageCode());
        userDto.setPhotoUrl(user.getPhotoUrl());
        userDto.setUsername(user.getUsername());
        dto.setUser(userDto);
        dto.setSubscription(user.getSubscription());
        return dto;
    }
}