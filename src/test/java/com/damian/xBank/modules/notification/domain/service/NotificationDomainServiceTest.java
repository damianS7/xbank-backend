package com.damian.xBank.modules.notification.domain.service;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("deleteNotification should delete a notification")
    void deleteNotification_Valid_DeletesNotification() {
        // given

        Notification notification = Notification.create(customer)
                                                .setId(1L);
        // when
        notificationDomainService.deleteNotification(customer.getAccount().getId(), notification);

        // then

    }

    @Test
    @DisplayName("deleteNotification should delete a notification")
    void deleteNotification_NotOwner_ThrowsException() {
        // given

        Notification notification = Notification.create(customer)
                                                .setId(1L);
        // when
        notificationDomainService.deleteNotification(customer.getAccount().getId(), notification);

        // then

    }
}
