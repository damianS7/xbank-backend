package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.user.application.dto.request.UserRegistrationRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.service.UserDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegister {
    private static final Logger log = LoggerFactory.getLogger(UserRegister.class);
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final SettingDomainService settingDomainService;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;

    public UserRegister(
            UserRepository userRepository,
            UserDomainService userDomainService,
            SettingDomainService settingDomainService,
            UserTokenLinkBuilder userTokenLinkBuilder,
            UserTokenVerificationNotifier userTokenVerificationNotifier
    ) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.settingDomainService = settingDomainService;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
    }

    /**
     * Creates a new user
     *
     * @param request contains the fields needed for the user creation
     * @return the user created
     * @throws UserProfileException if another user has the email
     */
    @Transactional
    public User execute(UserRegistrationRequest request) {
        // check if the email is already taken
        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailTakenException(request.email());
        }

        User user = userDomainService.createUserAccount(
                request.email(),
                request.password(),
                UserRole.CUSTOMER
        );

        // we create the user and assign the data
        UserProfile profile = UserProfile.create()
                                         .setUser(user)
                                         .setNationalId(request.nationalId())
                                         .setFirstName(request.firstName())
                                         .setLastName(request.lastName())
                                         .setPhone(request.phoneNumber())
                                         .setGender(request.gender())
                                         .setBirthdate(request.birthdate())
                                         .setCountry(request.country())
                                         .setAddress(request.address())
                                         .setPostalCode(request.zipCode())
                                         .setPhotoPath("avatar.jpg");

        user.setProfile(profile);

        // Create default settings for the new user
        settingDomainService.initializeDefaultSettingsFor(user);

        // settingRepository.save(setting);

        User registeredUser = userRepository.save(user);

        // Create a token for the account activation
        UserToken userToken = new UserToken(registeredUser);
        userToken.generateVerificationToken();
        // TODO review this
        //        userTokenRepository.save(userToken);

        // create the verification link
        String verificationLink = userTokenLinkBuilder.buildAccountVerificationLink(userToken.getToken());

        // Send email to the user with verification link
        userTokenVerificationNotifier.sendVerificationToken(request.email(), verificationLink);

        log.debug(
                "user: {} with email:{} registered",
                registeredUser.getId(),
                registeredUser.getEmail()
        );

        return registeredUser;
    }
}
