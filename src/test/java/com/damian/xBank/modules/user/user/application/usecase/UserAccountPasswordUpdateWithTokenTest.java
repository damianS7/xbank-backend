package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.token.domain.model.UserAccountTokenType;
import com.damian.xBank.modules.user.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserAccountPasswordUpdateWithTokenTest extends AbstractServiceTest {

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountPasswordUpdateWithToken userAccountPasswordUpdateWithToken;

    @Mock
    private UserAccountTokenService userAccountTokenService;

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
    @DisplayName("Should set a new password after reset password")
    void shouldSetPasswordAfterGeneratePasswordResetToken() {
        // given
        final String rawNewPassword = "1111000";
        final String encodedNewPassword = bCryptPasswordEncoder.encode(rawNewPassword);

        UserAccountPasswordResetSetRequest passwordResetRequest = new UserAccountPasswordResetSetRequest(
                rawNewPassword
        );

        UserAccountToken token = new UserAccountToken(customer);
        token.setToken(token.generateToken());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        // when
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userAccountTokenService.validateToken(token.getToken())).thenReturn(token);
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userAccountRepository.save(any(User.class))).thenReturn(customer);
        doNothing().when(emailSenderService).send(anyString(), anyString(), anyString());
        userAccountPasswordUpdateWithToken.execute(token.getToken(), passwordResetRequest);

        // then
        assertEquals(customer.getPassword(), encodedNewPassword);
    }
}
