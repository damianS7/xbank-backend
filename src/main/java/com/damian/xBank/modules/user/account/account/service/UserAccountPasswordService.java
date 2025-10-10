package com.damian.whatsapp.modules.user.account.account.service;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordResetRequest;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenRepository;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenType;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.infrastructure.mail.EmailSenderService;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountPasswordService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountPasswordService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final Environment env;
    private final UserAccountVerificationService userAccountVerificationService;

    public UserAccountPasswordService(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserRepository userRepository,
            EmailSenderService emailSenderService,
            UserAccountTokenRepository userAccountTokenRepository,
            Environment env,
            UserAccountVerificationService userAccountVerificationService
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.env = env;
        this.userAccountVerificationService = userAccountVerificationService;
    }

    /**
     * It updates the password of given user.
     *
     * @param userId   the id of the user to be updated
     * @param password the new password to be set
     * @throws UserNotFoundException                           if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(Long userId, String password) {
        // we get the UserAuth entity so we can save.
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Failed to update password. No user found with id: {}", userId);
                    return new UserNotFoundException(
                            Exceptions.USER.NOT_FOUND, userId
                    );
                }
        );

        // set the new password
        user.getAccount().setPassword(
                bCryptPasswordEncoder.encode(password)
        );

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        userRepository.save(user);
        log.debug("Successfully updated password for user: {}", userId);
    }

    /**
     * It updates the password of the current user
     *
     * @param request the request body that contains the current password and the new password
     * @throws UserNotFoundException                           if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(UserAccountPasswordUpdateRequest request) {
        // we extract the email from the User stored in the SecurityContext
        final User currentUser = AuthHelper.getLoggedUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        AuthHelper.validatePassword(currentUser, request.currentPassword());

        // update the password
        this.updatePassword(currentUser.getId(), request.newPassword());
    }

    /**
     * It resets the user password using a token.
     *
     * @param token   the token used to reset the password
     * @param request the request with the password to set
     */
    public void passwordResetWithToken(String token, UserAccountPasswordResetSetRequest request) {
        log.debug("Resetting password using a token.");
        // verify the token
        final UserAccountToken userAccountToken = userAccountVerificationService.validateToken(token);

        // update the password
        this.updatePassword(userAccountToken.getAccount().getUserId(), request.password());

        // set the token as used
        userAccountToken.setUsed(true);
        userAccountTokenRepository.save(userAccountToken);

        // send the email notifying the user that his password is successfully changed
        this.sendResetPasswordSuccessEmail(userAccountToken.getAccount().getEmail());
        log.debug("Password reset successfully.");
    }

    /**
     * Generate a token for password reset
     *
     * @param request the request containing the email of the user and password
     * @return AccountToken with the token
     */
    public UserAccountToken generatePasswordResetToken(UserAccountPasswordResetRequest request) {
        log.debug("Generating password reset token for email: {}", request.email());
        User user = userRepository
                .findByUserAccount_Email(request.email())
                .orElseThrow(
                        () -> {
                            log.error(
                                    "Failed to generate password reset token. No account found for: {}",
                                    request.email()
                            );
                            return new UserAccountNotFoundException(Exceptions.ACCOUNT.NOT_FOUND, request.email());
                        }
                );

        // generate the token for password reset
        UserAccountToken token = new UserAccountToken(user.getAccount());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        log.debug("Password reset token generated successfully for email: {}", request.email());
        return userAccountTokenRepository.save(token);
    }

    /**
     * Send email to the user with a link to reset password.
     *
     * @param toEmail the user's email address to send the email
     * @param token   the token to be included in the email
     */
    public void sendResetPasswordEmail(String toEmail, String token) {
        String host = env.getProperty("app.frontend.host");
        String port = env.getProperty("app.frontend.port");
        String url = String.format("http://%s:%s", host, port);
        String link = url + "/accounts/password/reset/" + token;
        emailSenderService.send(
                toEmail,
                "Photogram account: Password reset request.",
                "Reset your password following this url: " + link
        );
    }

    /**
     * Send an email after successfully reset of the password
     *
     * @param toEmail the user's email address to send the email
     */
    public void sendResetPasswordSuccessEmail(String toEmail) {
        emailSenderService.send(
                toEmail,
                "Photogram account: password reset successfully.",
                "Your password has been reset."
        );
    }
}
