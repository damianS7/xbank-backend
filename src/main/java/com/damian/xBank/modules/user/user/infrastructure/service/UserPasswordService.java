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
     * Actualiza el password de un usuario.
     *
     * @param userId
     * @param password
     * @throws UserNotFoundException
     * @throws UserInvalidPasswordConfirmationException
     */
    public void updatePassword(Long userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> {
                log.warn("Failed to update password");
                return new UserNotFoundException(userId);
            }
        );

        // Cambia la password
        user.changePassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        log.debug("Successfully updated password for user: {}", userId);
    }
}
