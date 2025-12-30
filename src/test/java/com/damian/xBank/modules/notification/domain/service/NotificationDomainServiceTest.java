package com.damian.xBank.modules.notification.domain.service;

import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class NotificationDomainServiceTest extends AbstractServiceTest {

    @InjectMocks
    private NotificationDomainService notificationDomainService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);
    }

}
