package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.usecase.verification.request.RequestAccountVerification;
import com.damian.xBank.modules.user.token.application.usecase.verification.request.RequestAccountVerificationCommand;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestAccountVerificationTest extends AbstractServiceTest {
    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private RequestAccountVerification requestAccountVerification;

    @Spy
    private UserTokenFactory userTokenFactory;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should request user verification email when user is pending for verification")
    void requestAccountVerification_WhenUserPendingVerification_SendsEmail() {
        // given
        User user = UserTestBuilder.builder()
            .withEmail("user@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withRole(UserRole.CUSTOMER)
            .build();

        RequestAccountVerificationCommand command = new RequestAccountVerificationCommand(
            user.getEmail()
        );

        UserToken token = userTokenFactory.verificationToken(user);

        // when
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(token);

        requestAccountVerification.execute(command);

        // then
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
    }

}
