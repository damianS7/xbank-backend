package com.damian.xBank.modules.user.user.infrastructure.mapper;

import com.damian.xBank.modules.user.user.application.usecase.UserResult;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUserCommand;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateUserEmailCommand;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateUserPasswordCommand;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.RegisterUserRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.UserPasswordUpdateRequest;

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

    public static RegisterUserCommand toCommand(RegisterUserRequest request) {
        return new RegisterUserCommand(
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
