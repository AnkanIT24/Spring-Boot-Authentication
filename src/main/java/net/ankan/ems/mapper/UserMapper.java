package net.ankan.ems.mapper;

import net.ankan.ems.dto.UserResponseDto;
import net.ankan.ems.entity.User;

public class UserMapper {

    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}