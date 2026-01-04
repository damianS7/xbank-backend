package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserPasswordServiceTest extends AbstractServiceTest {

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

    @Test
    @DisplayName("Should update account password")
    void shouldUpdateAccountPassword() {
        // given
        final String rawNewPassword = "1234";
        final String encodedNewPassword = bCryptPasswordEncoder.encode(rawNewPassword);

        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(user);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userAccountRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userAccountPasswordService.updatePassword(user.getId(), updateRequest.newPassword());

        // then
        verify(userAccountRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should not update password when account not found")
    void shouldNotUpdatePasswordWhenAccountNotFound() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

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
                        user.getId(), updateRequest.newPassword()
                )
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }
}
