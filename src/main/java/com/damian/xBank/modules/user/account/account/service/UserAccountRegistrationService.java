package com.damian.whatsapp.modules.user.account.account.service;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.user.service.UserService;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserAccountRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountRegistrationService.class);
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserService userService;

    public UserAccountRegistrationService(
            UserAccountVerificationService userAccountVerificationService,
            UserService userService
    ) {
        this.userAccountVerificationService = userAccountVerificationService;
        this.userService = userService;
    }

    /**
     * Register a new user account.
     *
     * @param request Contains the fields needed for the user account creation
     * @return User The user account created
     */
    public User registerAccount(UserAccountRegistrationRequest request) {
        log.debug("Registering a new account");
        // It uses the user account service to create a new user account
        User registeredUser = userService.createUser(request);

        // Create a token for the account activation
        UserAccountToken userAccountToken = userAccountVerificationService.generateVerificationToken(request.email());

        // send the account activation link
        userAccountVerificationService.sendAccountVerificationLinkEmail(request.email(), userAccountToken.getToken());

        log.debug(
                "user: {} with email:{} registered",
                registeredUser.getId(),
                registeredUser.getAccount().getEmail()
        );
        return registeredUser;
    }
}
