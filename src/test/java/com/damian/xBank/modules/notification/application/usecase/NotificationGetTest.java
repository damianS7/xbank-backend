package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NotificationGetTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private NotificationSinkRegistry notificationSinkRegistry;

    @InjectMocks
    private NotificationGet notificationGet;

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
    @DisplayName("should get notifications")
    void shouldGetNotifications() {
        // given
        setUpContext(customer.getAccount());

        // when
        Pageable pageable = mock(Pageable.class);
        Page<Notification> page = new PageImpl<>(
                List.of(
                        Notification.create(customer),
                        Notification.create(customer),
                        Notification.create(customer)
                )
        );

        when(notificationRepository.findAllByUserId(customer.getId(), pageable)).thenReturn(page);

        Page<Notification> result = notificationGet.getNotifications(pageable);

        // then
        assertThat(result).isEqualTo(page);
        assertThat(result.getTotalElements()).isEqualTo(page.getTotalElements());
        verify(notificationRepository).findAllByUserId(customer.getId(), pageable);
    }

    @Test
    @DisplayName("getNotificationsForUser should create sink and remove it on cancel")
    void getNotificationsForUser_CreatesAndRemovesSink() {
        // given
        setUpContext(customer.getAccount()); // asegura que el contexto tiene el usuario actual
        Long userId = customer.getId();

        // initially, no sink exists
        assertThat(notificationSinkRegistry.getSinkForUser(userId)).isNull();

        // when
        Flux<NotificationEvent> flux = notificationGet.getNotificationsForUser();

        // then
        StepVerifier.create(flux)
                    .then(() -> {
                        // sink should exist after subscribing
                        Sinks.Many<NotificationEvent> sink = notificationSinkRegistry.getSinkForUser(userId);
                        assertThat(sink).isNotNull();
                    })
                    .thenCancel()
                    .verify();

        // sink should be removed after cancellation
        assertThat(notificationSinkRegistry.getSinkForUser(userId)).isNull();
    }
}
