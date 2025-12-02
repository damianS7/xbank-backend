package com.damian.xBank.modules.user.account.account.dto.mapper;

import com.damian.xBank.modules.user.account.account.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.account.account.model.UserAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserAccountDtoMapper {
    public static UserAccountDto toUserDto(UserAccount user) {
        return new UserAccountDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static List<UserAccountDto> toUserDtoList(List<UserAccount> users) {
        return users
                .stream()
                .map(
                        UserAccountDtoMapper::toUserDto
                ).toList();
    }

    public static Page<UserAccountDto> toUserDtoWithPagination(Page<UserAccount> users) {
        return users.map(
                UserAccountDtoMapper::toUserDto
        );
    }
}
