package com.scrappy.scrappy.service.user;

import com.scrappy.scrappy.controller.dto.user.UserCreateDTO;
import com.scrappy.scrappy.controller.dto.user.UserDTO;
import com.scrappy.scrappy.controller.dto.user.UserMapper;
import com.scrappy.scrappy.domain.UserEntity;
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
        UserEntity user = userMapper.toEntity(createDTO);
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}