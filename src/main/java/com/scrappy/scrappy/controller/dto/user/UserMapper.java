package com.scrappy.scrappy.controller.dto.user;

import com.scrappy.scrappy.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserCreateDTO createDTO) {
        UserEntity user = new UserEntity();
        user.setUsername(createDTO.getUsername());
        return user;
    }

    public UserDTO toDto(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }
}