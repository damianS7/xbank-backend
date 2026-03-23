package com.damian.xBank.modules.user.token.application.usecase.verification.request;

/**
 * Comando para requerir correo de verificación
 *
 * @param email
 */
public record RequestAccountVerificationCommand(
    String email
) {
}
