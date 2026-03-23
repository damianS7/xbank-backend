package com.damian.xBank.modules.user.token.application.usecase.password.reset;

/**
 * This is the request used to reset password through email.
 * Email must match with the one in db otherwise nothing will be sent.
 * <p>
 * Comando usado para resetear el password usando el email
 *
 * @param email Correo donde se enviara el mensaje
 */
public record RequestPasswordResetCommand(
    String email
) {
}
