package com.damian.xBank.shared;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.DefaultPasswordValidator;
import com.damian.xBank.shared.security.PasswordValidator;
import com.damian.xBank.shared.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {

    @Spy
    protected PasswordValidator passwordValidator;

    @Mock
    protected AuthenticationContext authenticationContext;

    protected final String RAW_PASSWORD = "123456";

    @Spy
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    public AbstractServiceTest() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        passwordValidator = new DefaultPasswordValidator(bCryptPasswordEncoder);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    protected void setUpContext(UserPrincipal user) {
        when(authenticationContext.getUserPrincipal()).thenReturn(user);
    }

    protected void setUpContext(User user) {
        when(authenticationContext.getCurrentUser()).thenReturn(user);
    }
}
