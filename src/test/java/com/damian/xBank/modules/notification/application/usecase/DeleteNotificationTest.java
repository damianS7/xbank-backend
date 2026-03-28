package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotification;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotificationCommand;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.NotificationTestBuilder;
import com.damian.xBank.test.utils.UserTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteNotificationTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DeleteNotification deleteNotification;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomerWithId(1L);
    }

    @Test
    @DisplayName("should delete notification")
    void deleteNotification_WhenValid_DeletesNotification() {
        // given
        setUpContext(customer);

        Notification notification = NotificationTestBuilder.builder()
            .withId(1L)
            .withOwner(customer)
            .withType(NotificationType.TRANSFER)
            .withPayload(Map.of(
                "transactionId", 1L,
                "toUser", 1L,
                "amount", 100L,
                "currency", "EUR"
            ))
            .withTemplateKey("testTemplateKey")
            .build();

        DeleteNotificationCommand command = new DeleteNotificationCommand(
            notification.getId()
        );

        // when
        when(notificationRepository.findById(anyLong()))
            .thenReturn(Optional.of(notification));

        deleteNotification.execute(command);

        // then
        verify(notificationRepository).delete(any(Notification.class));
    }

    @Test
    @DisplayName("should throw exception when not owner")
    void deleteNotification_WhenNotOwner_ThrowsException() {
        // given
        setUpContext(customer);

        User anotherCustomer = UserTestBuilder.builder()
            .withId(99L)
            .withEmail("anotherCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        Notification notification = NotificationTestBuilder.builder()
            .withId(1L)
            .withOwner(anotherCustomer)
            .withType(NotificationType.TRANSFER)
            .withPayload(Map.of(
                "transactionId", 1L,
                "toUser", 1L,
                "amount", 100L,
                "currency", "EUR"
            ))
            .withTemplateKey("testTemplateKey")
            .build();

        DeleteNotificationCommand command = new DeleteNotificationCommand(
            notification.getId()
        );

        // when
        when(notificationRepository.findById(anyLong()))
            .thenReturn(Optional.of(notification));

        NotificationNotOwnerException exception = assertThrows(
            NotificationNotOwnerException.class,
            () -> deleteNotification.execute(command)

        );

        // then
        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.NOTIFICATION_NOT_OWNER);
        verify(notificationRepository, times(0)).delete(any(Notification.class));
    }
}
