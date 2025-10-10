package com.damian.whatsapp.modules.user.user.dto.mapper;

import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.shared.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getAccount().getEmail(),
                user.getAccount().getRole(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getBirthdate(),
                user.getGender(),
                user.getImageFilename(),
                user.getCreatedAt()
        );
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users
                .stream()
                .map(
                        UserDtoMapper::toUserDto
                ).toList();
    }

    public static Page<UserDto> toUserDtoWithPagination(Page<User> users) {
        return users.map(
                UserDtoMapper::toUserDto
        );
    }
}
