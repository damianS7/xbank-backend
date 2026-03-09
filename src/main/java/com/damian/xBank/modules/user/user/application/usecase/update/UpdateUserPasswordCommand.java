package com.damian.xBank.modules.user.user.application.usecase.update;

/**
 * Request used to set a new password.
 *
 * @param currentPassword this is the current password
 * @param newPassword     this is the new password
 */
public record UpdateUserPasswordCommand(
    String currentPassword,
    String newPassword
) {
}
