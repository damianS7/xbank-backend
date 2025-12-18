package com.damian.xBank.modules.user.account.application.service;

import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountEmailTakenException;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserAccountServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;
    private UserAccount userAccount;
    private Customer customer;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.create()
                                 .setId(2L)
                                 .setEmail("user@demo.com")
                                 .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        customer = Customer.create()
                           .setId(1L)
                           .setAccount(userAccount);
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() {
        // given
        // set the user on the context
        setUpContext(userAccount);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "david@test.com"
        );

        // when
        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.existsByEmail(anyString())).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        UserAccount updatedAccount = userAccountService.updateEmail(updateRequest);

        // then
        assertThat(updatedAccount)
                .isNotNull()
                .extracting(
                        UserAccount::getEmail
                ).isEqualTo(
                        updateRequest.newEmail()
                );

        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    @DisplayName("Should not update email when is already taken")
    void shouldNotUpdateEmailWhenIsAlreadyTaken() {
        // given

        // set the user on the context
        setUpContext(userAccount);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "david2@test.com"
        );

        // when
        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.existsByEmail(updateRequest.newEmail())).thenReturn(true);

        UserAccountEmailTakenException exception = assertThrows(
                UserAccountEmailTakenException.class,
                () -> userAccountService.updateEmail(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_EMAIL_TAKEN, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update email when password is wrong")
    void shouldNotUpdateEmailWhenPasswordIsWrong() {
        // given
        // set the user on the context
        setUpContext(userAccount);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                "wrong password",
                "david@test.com"
        );

        // when
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userAccountService.updateEmail(updateRequest)
        );

        // then
        assertEquals(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, exception.getMessage());
    }

}
