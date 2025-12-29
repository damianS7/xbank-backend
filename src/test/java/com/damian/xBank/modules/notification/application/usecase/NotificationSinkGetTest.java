package com.damian.xBank.modules.notification.application.usecase;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationSinkGetTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private NotificationSinkRegistry notificationSinkRegistry;

    @InjectMocks
    private NotificationSinkGet notificationSinkGet;

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
    @DisplayName("execute should create sink and remove it on cancel")
    void execute_CreatesAndRemovesSink() {
        // given
        setUpContext(customer.getAccount());
        Long userId = customer.getId();

        // initially, no sink exists
        assertThat(notificationSinkRegistry.getSinkForUser(userId)).isNull();

        // when
        Flux<NotificationEvent> flux = notificationSinkGet.execute();

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
