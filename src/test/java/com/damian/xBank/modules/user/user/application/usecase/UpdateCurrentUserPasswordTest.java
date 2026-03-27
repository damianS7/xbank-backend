package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.usecase.update.UpdateCurrentUserPassword;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateUserPasswordCommand;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.modules.user.utils.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UpdateCurrentUserPasswordTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordService userPasswordService;

    @InjectMocks
    private UpdateCurrentUserPassword updateCurrentUserPassword;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withPassword(RAW_PASSWORD)
            .withEmail("customer@demo.com")
            .build();
    }

    // TODO review this test
    @Test
    @DisplayName("should update user password")
    void passwordUpdate_SetsNewPassword() {
        // given
        setUpContext(customer);

        final String passwordHashBeforeUpdate = customer.getPasswordHash();
        final String rawNewPassword = "1234";

        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
            RAW_PASSWORD,
            rawNewPassword
        );

        // when
        when(bCryptPasswordEncoder.encode(anyString()))
            .thenAnswer(i -> i.getArgument(0));
        updateCurrentUserPassword.execute(command);

        // then
        assertThat(passwordHashBeforeUpdate)
            .isNotEqualTo(customer.getPasswordHash());
    }

    @Test
    @DisplayName("should throw exception when password confirmation failed")
    void passwordUpdate_WhenInvalidPassword_ThrowsException() {
        // given
        setUpContext(customer);

        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
            "wrongPassword",
            "1234"
        );

        // when
        UserInvalidPasswordConfirmationException exception = assertThrows(
            UserInvalidPasswordConfirmationException.class,
            () -> updateCurrentUserPassword.execute(command)
        );

        // then
        assertEquals(ErrorCodes.USER_INVALID_PASSWORD, exception.getMessage());
    }
}
