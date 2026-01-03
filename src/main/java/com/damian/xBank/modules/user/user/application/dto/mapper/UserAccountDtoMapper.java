package com.damian.xBank.modules.user.user.application.dto.mapper;

import com.damian.xBank.modules.user.user.application.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserAccountDtoMapper {
    public static UserAccountDto toUserDto(User user) {
        return new UserAccountDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static List<UserAccountDto> toUserDtoList(List<User> users) {
        return users
                .stream()
                .map(
                        UserAccountDtoMapper::toUserDto
                ).toList();
    }

    public static Page<UserAccountDto> toUserDtoWithPagination(Page<User> users) {
        return users.map(
                UserAccountDtoMapper::toUserDto
        );
    }
}
