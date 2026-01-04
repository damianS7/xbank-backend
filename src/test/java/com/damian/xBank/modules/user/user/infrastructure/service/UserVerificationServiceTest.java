package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountTokenService userAccountTokenService;

    @InjectMocks
    private UserAccountVerificationService userAccountVerificationService;


}
