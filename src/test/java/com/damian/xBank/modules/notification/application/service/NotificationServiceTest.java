package com.damian.xBank.modules.notification.application.service;

import com.damian.xBank.modules.notification.domain.entity.Notification;
import com.damian.xBank.modules.notification.domain.enums.NotificationType;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.notification.infra.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class NotificationServiceTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("should get notifications")
    void shouldGetNotifications() {
        // given
        UserAccount user = UserAccount.create()
                                      .setId(1L)
                                      .setEmail("publisher@demo.com")
                                      .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        setUpContext(user);

        // when
        Pageable pageable = mock(Pageable.class);
        Page<Notification> page = new PageImpl<>(
                List.of(
                        Notification.create(user),
                        Notification.create(user),
                        Notification.create(user)
                )
        );

        when(notificationRepository.findAllByUserId(user.getId(), pageable)).thenReturn(page);

        Page<Notification> result = notificationService.getNotifications(pageable);

        // then
        assertThat(result).isEqualTo(page);
        assertThat(result.getTotalElements()).isEqualTo(page.getTotalElements());
        verify(notificationRepository).findAllByUserId(user.getId(), pageable);
    }

    @Test
    @DisplayName("should delete notifications")
    void shouldDeleteNotifications() {
        // given
        UserAccount user = UserAccount.create()
                                      .setId(1L)
                                      .setEmail("publisher@demo.com")
                                      .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Notification notification = Notification.create(user)
                                                .setId(1L);

        setUpContext(user);
        notificationService.deleteNotifications(List.of(notification.getId()));
        verify(notificationRepository).deleteAllByIdInAndUser_Id(anyList(), anyLong());
    }

    @Test
    @DisplayName("should get notifications and removes sink on cancel")
    void shouldGetNotificationsAndRemovesSink() throws NoSuchFieldException, IllegalAccessException {
        // given
        UserAccount user = UserAccount.create()
                                      .setId(1L)
                                      .setEmail("publisher@demo.com")
                                      .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        setUpContext(user);

        Flux<NotificationEvent> flux = notificationService.getNotificationsForUser();

        // Usar StepVerifier para cancelar el Flux y verificar que el sink se elimina
        reactor.test.StepVerifier.create(flux)
                                 .thenCancel()
                                 .verify();

        // Access the private userSinks field using reflection
        java.lang.reflect.Field field = NotificationService.class.getDeclaredField("userSinks");
        field.setAccessible(true);
        Map<Long, ?> sinks = (Map<Long, ?>) field.get(notificationService);

        // Check that the sink for the user has been removed after cancellation
        assertThat(sinks.get(1L)).isNull();
    }

    @Test
    @DisplayName("should publish notification")
    void shouldPublishNotification() {
        // given
        UserAccount publisher = UserAccount.create()
                                           .setId(1L)
                                           .setEmail("publisher@demo.com")
                                           .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        //        setUpContext(publisher);

        UserAccount recipient = UserAccount.create()
                                           .setId(2L)
                                           .setEmail("recipient@demo.com")
                                           .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Map<String, Object> metadata = Map.of(
                "postId", 3L,
                "userName", "userName"
        );

        NotificationEvent event = new NotificationEvent(
                recipient.getId(),
                NotificationType.INFO,
                metadata,
                Instant.now().toString()
        );

        // when
        when(userAccountRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);
        notificationService.publish(event);

        // then
        verify(notificationRepository).save(any());
    }

    //    @Test
    //    @DisplayName("should not publish when publisher and recipient are the same")
    //    void shouldNotPublishNotificationWhenPublisherAndRecipientAreTheSame() {
    //        // given
    //        UserAccount publisher = UserAccount.create()
    //                                           .setId(1L)
    //                                           .setEmail("publisher@demo.com")
    //                                           .setPassword(passwordEncoder.encode(RAW_PASSWORD));
    //
    //        setUpContext(publisher);
    //
    //        Map<String, Object> metadata = Map.of(
    //                "postId", 1L,
    //                "userName", "userName"
    //        );
    //
    //        NotificationEvent event = new NotificationEvent(
    //                publisher.getId(), // same as publisher
    //                NotificationType.INFO,
    //                metadata,
    //                Instant.now().toString()
    //        );
    //        // when
    //        notificationService.publishNotification(event);
    //
    //        verify(notificationRepository, never()).save(any());
    //    }

    @Test
    @DisplayName("should not publish when recipient not found")
    void shouldNotPublishNotificationWhenRecipientNotFound() {
        // given
        UserAccount publisher = UserAccount.create()
                                           .setId(1L)
                                           .setEmail("publisher@demo.com")
                                           .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        //        setUpContext(publisher);

        Map<String, Object> metadata = Map.of(
                "postId", 1L,
                "userName", "userName"
        );

        NotificationEvent event = new NotificationEvent(
                2L, // same as publisher
                NotificationType.INFO,
                metadata,
                Instant.now().toString()
        );

        // when
        when(userAccountRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> notificationService.publish(event)
        );

        assertEquals(Exceptions.USER.ACCOUNT.NOT_FOUND, exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }

}
