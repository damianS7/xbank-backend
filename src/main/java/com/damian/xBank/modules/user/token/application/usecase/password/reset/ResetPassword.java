package com.damian.xBank.modules.user.token.application.usecase.password.reset;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para resetear la contraseña del usuario previo uso de {@link RequestPasswordReset}
 * <p>
 * Asigna una nueva password.
 */
@Service
public class ResetPassword {
    private static final Logger log = LoggerFactory.getLogger(ResetPassword.class);
    private final UserTokenRepository userTokenRepository;
    private final UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;
    private final UserTokenService userTokenService;
    private final UserPasswordService userPasswordService;

    public ResetPassword(
        UserTokenRepository userTokenRepository,
        UserTokenPasswordResetNotifier userTokenPasswordResetNotifier,
        UserTokenService userTokenService,
        UserPasswordService userPasswordService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userTokenPasswordResetNotifier = userTokenPasswordResetNotifier;
        this.userTokenService = userTokenService;
        this.userPasswordService = userPasswordService;
    }

    /**
     * @param command El comando con lo datos
     */
    @Transactional
    public void execute(ResetPasswordCommand command) {
        // Validar el token
        final UserToken userToken = userTokenService.validateToken(command.token());

        log.debug("Resetting password for user: {} using a token.", userToken.getUser().getId());

        // Cambiar el password
        userPasswordService.updatePassword(userToken.getUser().getId(), command.password());

        // User token
        userToken.markAsUsed();
        userTokenRepository.save(userToken);

        // Notificar
        userTokenPasswordResetNotifier.notifyPasswordReset(userToken.getUser().getEmail());
        log.debug("Password reset successfully.");
    }
}
