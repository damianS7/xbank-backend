package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenVerificationRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserTokenRequestVerificationTest extends AbstractServiceTest {
    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private UserTokenRequestVerification userTokenRequestVerification;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withPassword(RAW_PASSWORD)
                              .withEmail("customer@demo.com")
                              .build();
    }

    @Test
    @DisplayName("should request user verification email when user is pending for verification")
    void requestAccountVerification_WhenUserPendingVerification_SendsEmail() {
        // given
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        UserTokenVerificationRequest request = new UserTokenVerificationRequest(
                user.getEmail()
        );


        UserToken token = new UserToken(user);
        token.generateVerificationToken();

        // when
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(token);

        userTokenRequestVerification.execute(request);

        // then
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
    }

}
