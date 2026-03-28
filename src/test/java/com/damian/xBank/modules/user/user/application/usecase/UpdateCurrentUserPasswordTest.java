package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.usecase.update.UpdateCurrentUserPassword;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateUserPasswordCommand;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateCurrentUserPasswordTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordService userPasswordService;

    @InjectMocks
    private UpdateCurrentUserPassword updateCurrentUserPassword;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomerWithId(1L);
    }

    @Test
    @DisplayName("should update user password")
    void updatePassword_SetsNewPassword() {
        // given
        setUpContext(user);

        final String rawNewPassword = "1234";

        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand(
            RAW_PASSWORD,
            rawNewPassword
        );

        // when
        // then
        assertDoesNotThrow(() -> updateCurrentUserPassword.execute(command));
    }

    @Test
    @DisplayName("should throw exception when password confirmation failed")
    void updatePassword_WhenInvalidCurrentPassword_ThrowsException() {
        // given
        setUpContext(user);

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
