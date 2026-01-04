package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
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

public class UserAccountPasswordUpdateTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountPasswordUpdate userAccountPasswordUpdate;

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

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(customer);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        userAccountPasswordUpdate.execute(updateRequest);

        // then
        verify(userAccountRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Should not update password when current password failed")
    void shouldNotUpdatePasswordWhenPasswordConfirmationFailed() {
        // given
        // set the user on the context
        setUpContext(customer);

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                "wrongPassword",
                "1234"
        );

        // when
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userAccountPasswordUpdate.execute(updateRequest)
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

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234678Ax$"
        );

        when(userAccountRepository.findById(customer.getId()))
                .thenReturn(Optional.empty());

        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> userAccountPasswordUpdate.execute(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }
}
