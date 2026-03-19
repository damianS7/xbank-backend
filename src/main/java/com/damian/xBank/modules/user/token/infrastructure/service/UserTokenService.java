package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserTokenService {
    private static final Logger log = LoggerFactory.getLogger(UserTokenService.class);
    private final UserTokenRepository userTokenRepository;

    public UserTokenService(
        UserTokenRepository userTokenRepository
    ) {
        this.userTokenRepository = userTokenRepository;
    }

    /**
     * Comprueba que el token coincida con el almacenado en base de datos.
     * <p>
     * Hace comprobaciones para asegurar la validez del token.
     *
     * @param token
     * @return UserToken
     */
    public UserToken validateToken(String token) {
        log.debug("Validating token");

        // Buscar el token en base de datos
        UserToken userToken = userTokenRepository
            .findByToken(token)
            .orElseThrow(
                () -> {
                    log.error("Failed to validate token. Token {} not found.", token);
                    return new UserTokenNotFoundException();
                }
            );

        userToken.assertNotExpired();
        userToken.assertNotUsed();

        return userToken;
    }
}
