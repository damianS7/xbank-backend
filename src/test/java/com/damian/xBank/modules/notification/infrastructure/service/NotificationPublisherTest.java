package com.damian.xBank.modules.notification.infrastructure.service;

import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationPublisherTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationSinkRegistry notificationSinkRegistry;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomerWithId(1L);
    }

    @Test
    @DisplayName("publish saves notification when recipient exists")
    void publish_ValidRecipient_SavesNotification() {
        // given
        Map<String, Object> metadata = Map.of(
            "postId", 3L,
            "userName", "userName"
        );

        NotificationEvent event = new NotificationEvent(
            customer.getId(),
            NotificationType.TRANSFER,
            metadata,
            "notification.transfer.sent",
            Instant.now()
        );

        // when
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));
        when(notificationRepository.save(any()))
            .thenAnswer(i -> i.getArguments()[0]);
        notificationPublisher.publish(event);

        // then
        verify(notificationRepository).save(any());
    }

    @Test
    @DisplayName("publish throws exception when recipient is not found")
    void publish_RecipientNotFound_ThrowsException() {
        // given

        Map<String, Object> metadata = Map.of(
            "postId", 1L,
            "userName", "userName"
        );

        NotificationEvent event = new NotificationEvent(
            customer.getId(),
            NotificationType.TRANSFER,
            metadata,
            "notification.transfer.sent",
            Instant.now()
        );

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> notificationPublisher.publish(event)
        );

        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }
}
