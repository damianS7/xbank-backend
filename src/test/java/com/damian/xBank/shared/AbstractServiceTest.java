package com.damian.xBank.shared;

import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {
    protected final String RAW_PASSWORD = "123456";

    protected BCryptPasswordEncoder passwordEncoder;

    public AbstractServiceTest() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    protected void setUpContext(UserAccount userAccount) {
        User user = new User(userAccount);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
    }

    protected void setUpContext(Customer customer) {
        setUpContext(customer.getAccount());
    }

    protected void setUpContext(User user) {
        setUpContext(user.getAccount());
    }
}
