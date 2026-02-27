package com.damian.xBank.modules.user.user.application.cqrs.command;


public record UserEmailUpdateCommand(
    String currentPassword,
    String newEmail
) {
}
