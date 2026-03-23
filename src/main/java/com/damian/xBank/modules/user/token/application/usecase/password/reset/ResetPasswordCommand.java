package com.damian.xBank.modules.user.token.application.usecase.password.reset;

/**
 *
 * Comando para establecer una password usando un token
 *
 * @param password
 */
public record ResetPasswordCommand(
    String token,
    String password
) {
}
