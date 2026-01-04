package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenVerificationService;
import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
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
    private UserRepository userRepository;

    @InjectMocks
    private UserPasswordService userPasswordService;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserTokenVerificationService userTokenVerificationService;

    @Mock
    private UserTokenService userTokenService;

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

        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(user);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userPasswordService.updatePassword(user.getId(), updateRequest.newPassword());

        // then
        verify(userRepository, times(1)).save(user);
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

        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234678Ax$"
        );

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userPasswordService.updatePassword(
                        user.getId(), updateRequest.newPassword()
                )
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }
}
