package com.damian.xBank.modules.user.user.infrastructure.mapper;

import com.damian.xBank.modules.user.user.application.cqrs.command.UserEmailUpdateCommand;
import com.damian.xBank.modules.user.user.application.cqrs.command.UserPasswordUpdateCommand;
import com.damian.xBank.modules.user.user.application.cqrs.command.UserRegistrationCommand;
import com.damian.xBank.modules.user.user.application.cqrs.result.UserResult;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserRegistrationRequest;

public class UserDtoMapper {

    public static UserPasswordUpdateCommand toCommand(UserPasswordUpdateRequest request) {
        return new UserPasswordUpdateCommand(
            request.currentPassword(),
            request.newPassword()
        );
    }

    public static UserEmailUpdateCommand toCommand(UserEmailUpdateRequest request) {
        return new UserEmailUpdateCommand(
            request.currentPassword(),
            request.newEmail()
        );
    }

    public static UserRegistrationCommand toCommand(UserRegistrationRequest request) {
        return new UserRegistrationCommand(
            request.email(),
            request.password(),
            request.firstName(),
            request.lastName(),
            request.phoneNumber(),
            request.birthdate(),
            request.gender(),
            request.address(),
            request.zipCode(),
            request.country(),
            request.nationalId()
        );
    }

    public static UserResult toUserResult(User user) {
        return new UserResult(
            user.getId(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
