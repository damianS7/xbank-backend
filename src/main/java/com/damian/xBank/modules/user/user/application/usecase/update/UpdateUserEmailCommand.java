package com.damian.xBank.modules.user.user.application.usecase.update;


public record UpdateUserEmailCommand(
    String currentPassword,
    String newEmail
) {
}
