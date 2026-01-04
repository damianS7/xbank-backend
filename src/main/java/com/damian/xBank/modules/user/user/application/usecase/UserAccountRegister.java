package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.user.account.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.service.UserAccountDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountRegister {
    private static final Logger log = LoggerFactory.getLogger(UserAccountRegister.class);
    private final UserAccountRepository userRepository;
    private final UserAccountDomainService userAccountDomainService;
    private final SettingDomainService settingDomainService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserAccountTokenService userAccountTokenService;

    public UserAccountRegister(
            UserAccountRepository userRepository,
            UserAccountDomainService userAccountDomainService,
            SettingDomainService settingDomainService,
            UserAccountVerificationService userAccountVerificationService,
            UserAccountTokenService userAccountTokenService
    ) {
        this.userRepository = userRepository;
        this.userAccountDomainService = userAccountDomainService;
        this.settingDomainService = settingDomainService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userAccountTokenService = userAccountTokenService;
    }

    /**
     * Creates a new user
     *
     * @param request contains the fields needed for the user creation
     * @return the user created
     * @throws UserProfileException if another user has the email
     */
    @Transactional
    public User execute(UserAccountRegistrationRequest request) {
        // check if the email is already taken
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAccountEmailTakenException(request.email());
        }

        User user = userAccountDomainService.createUserAccount(
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

        // TODO usecase
        // settingRepository.save(setting);

        User registeredUser = userRepository.save(user);

        // Create a token for the account activation
        UserAccountToken userAccountToken = userAccountTokenService.generateVerificationToken(request.email());

        // send the account activation link
        userAccountVerificationService
                .sendVerificationLinkEmail(request.email(), userAccountToken.getToken());

        log.debug(
                "user: {} with email:{} registered",
                registeredUser.getId(),
                registeredUser.getEmail()
        );


        return userRepository.save(user);
    }
}
