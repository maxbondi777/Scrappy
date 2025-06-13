package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.UserCreateDTO;
import com.scrappy.scrappy.controller.dto.UserDTO;
import com.scrappy.scrappy.controller.dto.UserMapper;
import com.scrappy.scrappy.domain.User;
import com.scrappy.scrappy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO createDTO) {
        logger.debug("Creating user with username: {}", createDTO.getUsername());
        User user = userMapper.toEntity(createDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}