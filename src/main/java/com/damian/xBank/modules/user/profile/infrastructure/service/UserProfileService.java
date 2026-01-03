package com.damian.xBank.modules.user.profile.infrastructure.service;

import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.userProfileRepository = userProfileRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

}
