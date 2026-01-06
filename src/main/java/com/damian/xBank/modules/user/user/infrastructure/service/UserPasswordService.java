package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordService {
    private static final Logger log = LoggerFactory.getLogger(UserPasswordService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public UserPasswordService(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserRepository userRepository
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
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
        user.changePassword(bCryptPasswordEncoder.encode(password));

        // save the changes
        userRepository.save(user);
        log.debug("Successfully updated password for user: {}", userId);
    }
}
