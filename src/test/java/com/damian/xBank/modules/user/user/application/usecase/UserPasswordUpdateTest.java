package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserPasswordUpdateTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordService userPasswordService;

    @InjectMocks
    private UserPasswordUpdate userPasswordUpdate;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(RAW_PASSWORD)
                                  .withEmail("customer@demo.com")
                                  .withProfile(UserProfileFactory.testProfile())
                                  .build();
    }

    @Test
    @DisplayName("should update user password")
    void passwordUpdate_WhenValidRequest_NotThrows() {
        // given
        // set the user on the context
        setUpContext(customer);

        final String rawNewPassword = "1234";

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // when
        userPasswordUpdate.execute(request);

        // then
    }

    @Test
    @DisplayName("should throw exception when password confirmation failed")
    void passwordUpdate_WhenInvalidPassword_ThrowsException() {
        // given
        // set the user on the context
        setUpContext(customer);

        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                "wrongPassword",
                "1234"
        );

        // when
        UserInvalidPasswordConfirmationException exception = assertThrows(
                UserInvalidPasswordConfirmationException.class,
                () -> userPasswordUpdate.execute(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_INVALID_PASSWORD, exception.getMessage());
    }
}
