package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class NotificationDeleteTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationDelete notificationDelete;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@demo.com")
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .build();
    }

    @Test
    @DisplayName("should delete notification")
    void deleteNotification_WhenValid_DeletesNotification() {
        // given
        setUpContext(customer);

        Notification givenNotification = Notification.create(customer)
                                                     .setId(1L)
                                                     .setMessage("Hello world!");

        // when
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenNotification));

        notificationDelete.execute(givenNotification.getId());

        // then
        verify(notificationRepository).delete(any(Notification.class));
    }

    @Test
    @DisplayName("should throw exception when not owner")
    void deleteNotification_WhenNotOwner_ThrowsException() {
        // given
        setUpContext(customer);

        User anotherCustomer = UserTestBuilder.aCustomer()
                                              .withId(99L)
                                              .withEmail("anotherCustomer@demo.com")
                                              .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                              .build();

        Notification givenNotification = Notification.create(anotherCustomer)
                                                     .setId(1L)
                                                     .setMessage("Hello world!");

        // when
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenNotification));

        NotificationNotOwnerException exception = assertThrows(
                NotificationNotOwnerException.class,
                () -> notificationDelete.execute(givenNotification.getId())

        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.NOTIFICATION_NOT_OWNER);
        verify(notificationRepository, times(0)).delete(any(Notification.class));
    }
}
