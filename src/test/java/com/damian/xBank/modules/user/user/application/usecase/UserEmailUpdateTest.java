package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserEmailUpdateTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserEmailUpdate userEmailUpdate;

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
    @DisplayName("Should update email")
    void shouldUpdateEmail() {
        // given
        // set the user on the context
        setUpContext(customer);

        UserEmailUpdateRequest updateRequest = new UserEmailUpdateRequest(
                RAW_PASSWORD,
                "david@test.com"
        );

        // when
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        User updatedAccount = userEmailUpdate.execute(updateRequest);

        // then
        assertThat(updatedAccount)
                .isNotNull()
                .extracting(
                        User::getEmail
                ).isEqualTo(
                        updateRequest.newEmail()
                );

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not update email when is already taken")
    void shouldNotUpdateEmailWhenIsAlreadyTaken() {
        // given

        // set the user on the context
        setUpContext(customer);

        UserEmailUpdateRequest updateRequest = new UserEmailUpdateRequest(
                RAW_PASSWORD,
                "david2@test.com"
        );

        // when
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.existsByEmail(updateRequest.newEmail())).thenReturn(true);

        UserEmailTakenException exception = assertThrows(
                UserEmailTakenException.class,
                () -> userEmailUpdate.execute(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_EMAIL_TAKEN, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update email when password is wrong")
    void shouldNotUpdateEmailWhenPasswordIsWrong() {
        // given
        // set the user on the context
        setUpContext(customer);

        UserEmailUpdateRequest updateRequest = new UserEmailUpdateRequest(
                "wrong password",
                "david@test.com"
        );

        // when
        UserInvalidPasswordConfirmationException exception = assertThrows(
                UserInvalidPasswordConfirmationException.class,
                () -> userEmailUpdate.execute(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, exception.getMessage());
    }

}
