package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.account.account.service.UserAccountRegistrationService;
import com.damian.xBank.modules.user.account.account.service.UserAccountService;
import com.damian.xBank.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserAccountToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class UserAccountRegistrationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private UserAccountRegistrationService userAccountRegistrationService;

    @Mock
    private UserAccountService userService;

    @Mock
    private UserAccountVerificationService userAccountVerificationService;

    @Test
    @DisplayName("should register a new account")
    void shouldRegisterAccount() {
        // given
        UserAccount givenUserAccount = UserAccount.create()
                                                  .setEmail("user@test.com")
                                                  .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountRegistrationRequest registrationRequest = new UserAccountRegistrationRequest(
                givenUserAccount.getEmail(),
                givenUserAccount.getPassword()
        );

        UserAccountToken userAccountToken = UserAccountToken.create()
                                                            .setAccount(givenUserAccount);

        // when
        when(userAccountVerificationService.generateVerificationToken(anyString())).thenReturn(userAccountToken);
        when(userService.createUserAccount(any(UserAccountRegistrationRequest.class))).thenReturn(givenUserAccount);

        UserAccount registeredUser = userAccountRegistrationService.registerAccount(registrationRequest);

        // then
        assertThat(registeredUser)
                .isNotNull()
                .extracting(
                        UserAccount::getEmail,
                        UserAccount::getPassword
                ).containsExactly(
                        givenUserAccount.getEmail(),
                        givenUserAccount.getPassword()
                );

    }
}
