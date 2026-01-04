package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserPasswordService {
    private static final Logger log = LoggerFactory.getLogger(UserPasswordService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;
    private final EmailSenderService emailSenderService;
    private final UserTokenRepository userTokenRepository;
    private final Environment env;
    private final UserTokenService userTokenService;
    private final AuthenticationContext authenticationContext;

    public UserPasswordService(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserRepository userRepository,
            PasswordValidator passwordValidator,
            EmailSenderService emailSenderService,
            UserTokenRepository userTokenRepository,
            Environment env,
            UserTokenService userTokenService,
            AuthenticationContext authenticationContext
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
        this.emailSenderService = emailSenderService;
        this.userTokenRepository = userTokenRepository;
        this.env = env;
        this.authenticationContext = authenticationContext;
        this.userTokenService = userTokenService;
    }


    /**
     * It updates the password of given user.
     *
     * @param userId   the id of the user to be updated
     * @param password the new password to be set
     * @throws UserNotFoundException                    if the user does not exist
     * @throws UserInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(Long userId, String password) {
        // we get the UserAuth entity so we can save.
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Failed to update password");
                    return new UserNotFoundException(userId);
                }
        );

        // set the new password
        user.setPassword(bCryptPasswordEncoder.encode(password));

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        userRepository.save(user);
        log.debug("Successfully updated password for user: {}", userId);
    }
}
