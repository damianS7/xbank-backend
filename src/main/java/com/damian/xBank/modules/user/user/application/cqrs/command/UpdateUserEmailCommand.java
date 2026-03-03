package com.damian.xBank.modules.user.user.application.cqrs.command;


public record UpdateUserEmailCommand(
    String currentPassword,
    String newEmail
) {
}
