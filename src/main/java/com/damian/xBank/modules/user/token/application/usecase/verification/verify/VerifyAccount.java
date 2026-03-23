package com.damian.xBank.modules.user.token.application.usecase.verification.verify;

import com.damian.xBank.modules.user.token.application.usecase.verification.request.RequestAccountVerification;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para confirmar/verificar una cuenta a traves de un token.
 * Se ha de usar antes {@link RequestAccountVerification} o registrarse de nuevo
 */
@Service
public class VerifyAccount {
    private static final Logger log = LoggerFactory.getLogger(VerifyAccount.class);
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;

    public VerifyAccount(
        UserTokenRepository userTokenRepository,
        UserRepository userRepository,
        UserTokenService userTokenService,
        UserTokenVerificationNotifier userTokenVerificationNotifier
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.userTokenService = userTokenService;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
    }

    /**
     *
     * @throws UserTokenNotFoundException
     * @throws UserTokenExpiredException
     * @throws UserTokenUsedException
     * @throws UserVerificationNotPendingException
     */
    @Transactional
    public void execute(VerifyAccountCommand command) {
        // Validar token
        UserToken userToken = userTokenService.validateToken(command.token());

        // Usuario dueño del token
        User user = userToken.getUser();

        user.verifyAccount();
        userToken.markAsUsed();

        userTokenRepository.save(userToken);
        userRepository.save(user);

        // send email to user after user has been verified
        userTokenVerificationNotifier.notifyVerification(user.getEmail());

        log.debug("User: {} successfully verified.", user.getId());
    }
}
