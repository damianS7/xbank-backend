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
     * Validate if the token matches with the one in the database
     * also run checks for expiration and usage of the token.
     *
     * @param token the token to verify
     * @return AccountToken the token entity
     */
    public UserToken validateToken(String token) {
        log.debug("Validating token");
        // check the token if it matches with the one in database
        UserToken userToken = userTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> {
                            log.error("Failed to validate token. Token {} not found.", token);
                            return new UserTokenNotFoundException();
                        }
                );

        // check expiration
        userToken.assertNotExpired();

        // check if token is already used
        userToken.assertNotUsed();

        return userToken;
    }
}
