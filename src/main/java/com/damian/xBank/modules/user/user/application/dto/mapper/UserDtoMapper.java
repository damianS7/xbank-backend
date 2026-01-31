package com.damian.xBank.modules.user.user.application.dto.mapper;

import com.damian.xBank.modules.user.profile.application.dto.mapper.UserProfileDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.response.UserDetailDto;
import com.damian.xBank.modules.user.user.application.dto.response.UserDto;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static UserDetailDto toUserDetailDto(User user) {
        return new UserDetailDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                UserProfileDtoMapper.toUserProfileDto(user.getProfile()),
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
