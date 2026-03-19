package com.damian.xBank.modules.user.user.application.usecase.update;

/**
 * Comando para establecer una nueva password
 *
 * @param currentPassword this is the current password
 * @param newPassword     this is the new password
 */
public record UpdateUserPasswordCommand(
    String currentPassword,
    String newPassword
) {
}
