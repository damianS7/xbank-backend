package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationDeleteAllTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationDeleteAll notificationDeleteAll;

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
    @DisplayName("should delete notifications")
    void deleteNotifications_ValidRequest_DeleteNotifications() {
        // given
        setUpContext(customer.getAccount());

        Notification notification = Notification.create(customer)
                                                .setId(1L);

        // when
        notificationDeleteAll.execute(List.of(notification.getId()));

        // then
        verify(notificationRepository).deleteAllByIdInAndUser_Id(anyList(), anyLong());
    }
}
