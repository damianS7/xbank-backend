package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.service.NotificationDomainService;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
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
    @DisplayName("deleteNotification should delete notification by id")
    void deleteNotification_Valid_DeletesNotification() {
        // given
        setUpContext(customer.getAccount());

        Notification givenNotification = Notification.create(customer)
                                                     .setId(1L)
                                                     .setMessage("Hello world!");

        // when
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenNotification));

        notificationDelete.deleteNotification(givenNotification.getId());

        // then
        verify(notificationRepository).delete(any(Notification.class));
    }

    @Test
    @DisplayName("deleteNotification should throws when not owner")
    void deleteNotification_NotOwner_ThrowsException() {
        // given
        setUpContext(customer.getAccount());

        Customer anotherCustomer = Customer.create(
                UserAccount.create()
                           .setId(99L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(99L);

        Notification givenNotification = Notification.create(anotherCustomer)
                                                     .setId(1L)
                                                     .setMessage("Hello world!");

        // when
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenNotification));

        NotificationNotOwnerException exception = assertThrows(
                NotificationNotOwnerException.class,
                () -> notificationDelete.deleteNotification(givenNotification.getId())

        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.NOTIFICATION_NOT_OWNER);
        verify(notificationRepository, times(0)).delete(any(Notification.class));
    }

    @Test
    @DisplayName("should delete notifications")
    void deleteNotifications_Valid_DeletesNotification() {
        // given
        setUpContext(customer.getAccount());

        Notification notification = Notification.create(customer)
                                                .setId(1L);

        // when
        notificationDelete.deleteNotifications(List.of(notification.getId()));

        // then
        verify(notificationRepository).deleteAllByIdInAndUser_Id(anyList(), anyLong());
    }

}
