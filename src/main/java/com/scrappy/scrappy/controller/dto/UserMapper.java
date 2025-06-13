package com.scrappy.scrappy.controller.dto;

import com.scrappy.scrappy.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateDTO createDTO) {
        User user = new User();
        user.setUsername(createDTO.getUsername());
        return user;
    }

    public UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }
}