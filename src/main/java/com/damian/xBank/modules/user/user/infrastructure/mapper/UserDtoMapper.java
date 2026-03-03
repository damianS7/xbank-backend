package com.damian.xBank.modules.user.user.infrastructure.mapper;

import com.damian.xBank.modules.user.user.application.cqrs.command.UpdateUserEmailCommand;
import com.damian.xBank.modules.user.user.application.cqrs.command.UpdateUserPasswordCommand;
import com.damian.xBank.modules.user.user.application.cqrs.command.UserRegistrationCommand;
import com.damian.xBank.modules.user.user.application.cqrs.result.UserResult;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserRegistrationRequest;

public class UserDtoMapper {

    public static UpdateUserPasswordCommand toCommand(UserPasswordUpdateRequest request) {
        return new UpdateUserPasswordCommand(
            request.currentPassword(),
            request.newPassword()
        );
    }

    public static UpdateUserEmailCommand toCommand(UserEmailUpdateRequest request) {
        return new UpdateUserEmailCommand(
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
