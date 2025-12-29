package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.service.NotificationDomainService;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class NotificationDeleteTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private NotificationDomainService notificationDomainService;

    @InjectMocks
    private NotificationDelete notificationDelete;

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
    @DisplayName("should delete notification by id")
    void shouldDeleteNotificationById() {
        // given
        setUpContext(customer.getAccount());

        Notification notification = Notification.create(customer)
                                                .setId(1L);

        // when
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(notification));

        notificationDelete.deleteNotification(notification.getId());

        // then
        verify(notificationRepository).delete(any(Notification.class));
    }

    @Test
    @DisplayName("should delete notifications")
    void shouldDeleteNotifications() {
        // given
        setUpContext(customer.getAccount());

        Notification notification = Notification.create(customer)
                                                .setId(1L);
        notificationDelete.deleteNotifications(List.of(notification.getId()));
        verify(notificationRepository).deleteAllByIdInAndUser_Id(anyList(), anyLong());
    }

}
