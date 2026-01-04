package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserTokenService userTokenService;

    @InjectMocks
    private UserAccountVerificationService userAccountVerificationService;


}
