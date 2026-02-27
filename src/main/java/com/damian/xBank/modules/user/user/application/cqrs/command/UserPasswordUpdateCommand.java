package com.damian.xBank.modules.user.user.application.cqrs.command;

/**
 * Request used to set a new password.
 *
 * @param currentPassword this is the current password
 * @param newPassword     this is the new password
 */
public record UserPasswordUpdateCommand(
    String currentPassword,
    String newPassword
) {
}
