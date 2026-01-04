package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserPasswordUpdateTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPasswordUpdate userPasswordUpdate;

    private User customer;

    @BeforeEach
    void setUp() {

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .withEmail("customer@demo.com")
                                  .withProfile(UserProfileTestFactory.aProfile())
                                  .build();
    }

    @Test
    @DisplayName("Should update account password")
    void shouldUpdateAccountPassword() {
        // given
        final String rawNewPassword = "1234";
        final String encodedNewPassword = bCryptPasswordEncoder.encode(rawNewPassword);

        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(customer);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        userPasswordUpdate.execute(updateRequest);

        // then
        verify(userRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Should not update password when current password failed")
    void shouldNotUpdatePasswordWhenPasswordConfirmationFailed() {
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
        assertEquals(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update password when account not found")
    void shouldNotUpdatePasswordWhenAccountNotFound() {
        // given
        // set the user on the context
        setUpContext(customer);

        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234678Ax$"
        );

        when(userRepository.findById(customer.getId()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userPasswordUpdate.execute(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }
}
