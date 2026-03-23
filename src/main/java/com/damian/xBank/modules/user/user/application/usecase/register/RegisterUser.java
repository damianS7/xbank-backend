package com.damian.xBank.modules.user.user.application.usecase.register;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el usuario se registra
 */
@Service
public class RegisterUser {
    private static final Logger log = LoggerFactory.getLogger(RegisterUser.class);
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;
    private final UserTokenFactory userTokenFactory;

    public RegisterUser(
        UserRepository userRepository,
        UserDomainService userDomainService,
        UserTokenLinkBuilder userTokenLinkBuilder,
        UserTokenVerificationNotifier userTokenVerificationNotifier,
        UserTokenFactory userTokenFactory
    ) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
        this.userTokenFactory = userTokenFactory;
    }

    /**
     * Crea un nuevo usuario
     *
     * @param command
     * @return
     * @throws UserProfileException
     */
    @Transactional
    public RegisterUserResult execute(RegisterUserCommand command) {
        // Comprobar email
        if (userRepository.existsByEmail(command.email())) {
            throw new UserEmailTakenException(command.email());
        }

        // Crear el perfil
        UserProfile profile = UserProfile.create(
            command.firstName(),
            command.lastName(),
            command.phoneNumber(),
            command.birthdate(),
            "avatar.jpg",
            command.address(),
            command.zipCode(),
            command.country(),
            command.nationalId(),
            command.gender()
        );

        // Crear usuario
        User user = userDomainService.createUser(
            command.email(),
            command.password(),
            UserRole.CUSTOMER,
            profile
        );

        // Generar token de verificación
        UserToken userToken = userTokenFactory.verificationToken(user);
        String verificationLink = userTokenLinkBuilder.buildAccountVerificationLink(userToken.getToken());
        userRepository.save(user);


        // Notificar usuario
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
}
