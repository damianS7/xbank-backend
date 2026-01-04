package com.damian.xBank.modules.notification.infrastructure.service;

import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
import static org.mockito.Mockito.*;

public class NotificationPublisherTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private NotificationSinkRegistry notificationSinkRegistry;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

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
    @DisplayName("publish saves notification when recipient exists")
    void publish_ValidRecipient_SavesNotification() {
        // given
        Map<String, Object> metadata = Map.of(
                "postId", 3L,
                "userName", "userName"
        );

        NotificationEvent event = new NotificationEvent(
                customer.getId(),
                NotificationType.INFO,
                metadata,
                Instant.now().toString()
        );

        // when
        when(userAccountRepository.findById(anyLong())).thenReturn(Optional.of(customer));
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
                NotificationType.INFO,
                metadata,
                Instant.now().toString()
        );

        // when
        when(userAccountRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> notificationPublisher.publish(event)
        );

        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }
}
