package com.damian.xBank.modules.user.account.account.application.service;

import com.damian.xBank.infrastructure.mail.EmailSenderService;
import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.application.service.UserAccountTokenService;
import com.damian.xBank.modules.user.account.token.domain.entity.UserAccountToken;
import com.damian.xBank.modules.user.account.token.infra.repository.UserAccountTokenRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import com.damian.xBank.shared.security.User;
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
    private final UserAccountRepository userAccountRepository;
    private final PasswordValidator passwordValidator;
    private final EmailSenderService emailSenderService;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final Environment env;
    private final UserAccountTokenService userAccountTokenService;
    private final AuthenticationContext authenticationContext;

    public UserAccountPasswordService(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserAccountRepository userAccountRepository,
            PasswordValidator passwordValidator,
            EmailSenderService emailSenderService,
            UserAccountTokenRepository userAccountTokenRepository,
            Environment env,
            UserAccountTokenService userAccountTokenService,
            AuthenticationContext authenticationContext
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userAccountRepository = userAccountRepository;
        this.passwordValidator = passwordValidator;
        this.emailSenderService = emailSenderService;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.env = env;
        this.authenticationContext = authenticationContext;
        this.userAccountTokenService = userAccountTokenService;
    }

    /**
     * It updates the password of given user.
     *
     * @param userId   the id of the user to be updated
     * @param password the new password to be set
     * @throws UserAccountNotFoundException                    if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(Long userId, String password) {
        // we get the UserAuth entity so we can save.
        UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Failed to update password");
                    return new UserAccountNotFoundException(userId);
                }
        );

        // set the new password
        userAccount.setPassword(
                bCryptPasswordEncoder.encode(password)
        );

        // we change the updateAt timestamp field
        userAccount.setUpdatedAt(Instant.now());

        // save the changes
        userAccountRepository.save(userAccount);
        log.debug("Successfully updated password for user: {}", userId);
    }

    /**
     * It updates the password of the current user
     *
     * @param request the request body that contains the current password and the new password
     * @throws UserAccountNotFoundException                    if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(UserAccountPasswordUpdateRequest request) {
        // we extract the email from the User stored in the SecurityContext
        final User currentUser = authenticationContext.getCurrentUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, request.currentPassword());

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
        final UserAccountToken userAccountToken = userAccountTokenService.validateToken(token);

        // update the password
        this.updatePassword(userAccountToken.getAccount().getId(), request.password());

        // set the token as used
        userAccountToken.setUsed(true);
        userAccountTokenRepository.save(userAccountToken);

        // send the email notifying the user that his password is successfully changed
        this.sendResetPasswordSuccessEmail(userAccountToken.getAccount().getEmail());
        log.debug("Password reset successfully.");
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
