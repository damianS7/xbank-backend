package com.damian.xBank.modules.user.user.application.usecase.register;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileException;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.service.UserDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUser {
    private static final Logger log = LoggerFactory.getLogger(RegisterUser.class);
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;
    private final UserProfileFactory userProfileFactory;
    private final UserTokenFactory userTokenFactory;

    public RegisterUser(
        UserRepository userRepository,
        UserDomainService userDomainService,
        UserTokenLinkBuilder userTokenLinkBuilder,
        UserTokenVerificationNotifier userTokenVerificationNotifier,
        UserProfileFactory userProfileFactory,
        UserTokenFactory userTokenFactory
    ) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
        this.userProfileFactory = userProfileFactory;
        this.userTokenFactory = userTokenFactory;
    }

    /**
     * Creates a new user
     *
     * @param command contains the fields needed for the user creation
     * @return the user created
     * @throws UserProfileException if another user has the email
     */
    @Transactional
    public RegisterUserResult execute(RegisterUserCommand command) {
        // check if the email is already taken
        if (userRepository.existsByEmail(command.email())) {
            throw new UserEmailTakenException(command.email());
        }

        // Create the user
        User user = userDomainService.createUser(
            command.email(),
            command.password(),
            UserRole.CUSTOMER
        );

        // create the user profile
        UserProfile profile = userProfileFactory.create(command);
        user.assignProfile(profile);

        // Create default settings for the new user
        //        Setting userSettings = settingFactory.createDefault();
        //        user.setSettings(userSettings);

        // Create a token for the account activation
        UserToken userToken = userTokenFactory.verificationToken();
        user.setToken(userToken);

        // save (cascade)
        userRepository.save(user);

        // create the verification link
        String verificationLink = userTokenLinkBuilder.buildAccountVerificationLink(userToken.getToken());

        // Send email to the user with verification link
        userTokenVerificationNotifier.sendVerificationToken(command.email(), verificationLink);

        log.debug(
            "user: {} with email:{} registered",
            user.getId(),
            user.getEmail()
        );

        return new RegisterUserResult(
            user.getId(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    @Async  // ← No bloquea ni causa rollback si falla
    private void sendVerificationEmailAsync(User user) {
        try {
            //            userTokenVerificationNotifier.sendVerificationToken(request.email(), verificationLink);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", user.getEmail(), e);
            // Podría publicar evento para retry
        }
    }
}
