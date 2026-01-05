package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenResetPasswordRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenPasswordNotifier;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class UserTokenResetPasswordTest extends AbstractServiceTest {

    @Mock
    private UserTokenPasswordNotifier userTokenPasswordNotifier;

    @Mock
    private UserPasswordService userPasswordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private UserTokenResetPassword userTokenResetPassword;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(RAW_PASSWORD)
                                  .withEmail("customer@demo.com")
                                  .withProfile(UserProfileTestFactory.aProfile())
                                  .build();
    }

    @Test
    @DisplayName("should set a new password when token is valid")
    void resetPassword_WhenValidToken_ResetsPasswordAndMarksTokenAsUsed() {
        // given
        UserTokenResetPasswordRequest passwordResetRequest = new UserTokenResetPasswordRequest(
                "1111000"
        );

        UserToken token = new UserToken(customer);
        token.generateResetPasswordToken();

        // when
        when(userTokenService.validateToken(token.getToken())).thenReturn(token);

        userTokenResetPassword.execute(token.getToken(), passwordResetRequest);

        // then
        assertThat(token.isUsed()).isTrue();
    }
}
