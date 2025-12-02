package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.account.account.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.model.UserAccount;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.account.service.UserAccountPasswordService;
import com.damian.xBank.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.xBank.modules.user.account.token.enums.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.service.UserAccountTokenService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserAccountPasswordServiceTest extends AbstractServiceTest {

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountPasswordService userAccountPasswordService;

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountVerificationService userAccountVerificationService;

    @Mock
    private UserAccountTokenService userAccountTokenService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("Should update account password")
    void shouldUpdateAccountPassword() {
        // given
        final String rawNewPassword = "1234";
        final String encodedNewPassword = passwordEncoder.encode(rawNewPassword);

        UserAccount user = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(user);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userAccountRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userAccountPasswordService.updatePassword(updateRequest);

        // then
        verify(userAccountRepository, times(1)).save(user);
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
    }

    @Test
    @DisplayName("Should not update password when current password failed")
    void shouldNotUpdatePasswordWhenPasswordConfirmationFailed() {
        // given
        UserAccount user = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        // set the user on the context
        setUpContext(user);

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                "wrongPassword",
                "1234"
        );

        // when
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userAccountPasswordService.updatePassword(
                        updateRequest
                )
        );
        // then
        assertEquals(Exceptions.USER.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update password when account not found")
    void shouldNotUpdatePasswordWhenAccountNotFound() {
        // given
        UserAccount user = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        // set the user on the context
        setUpContext(user);

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234678Ax$"
        );

        when(userAccountRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> userAccountPasswordService.updatePassword(
                        updateRequest
                )
        );

        // then
        assertEquals(Exceptions.USER.ACCOUNT.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should set a new password after reset password")
    void shouldSetPasswordAfterGeneratePasswordResetToken() {
        // given
        final String rawNewPassword = "1111000";
        final String encodedNewPassword = passwordEncoder.encode(rawNewPassword);

        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordResetSetRequest passwordResetRequest = new UserAccountPasswordResetSetRequest(
                rawNewPassword
        );

        UserAccountToken token = new UserAccountToken(userAccount);
        token.setToken(token.generateToken());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        // when
        //        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(userAccountTokenService.validateToken(token.getToken())).thenReturn(token);
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        doNothing().when(emailSenderService).send(anyString(), anyString(), anyString());
        userAccountPasswordService.passwordResetWithToken(token.getToken(), passwordResetRequest);

        // then
        assertEquals(userAccount.getPassword(), encodedNewPassword);
    }
}
