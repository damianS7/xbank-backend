package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenService userTokenService;

    @InjectMocks
    private UserTokenVerificationService userTokenVerificationService;


}
