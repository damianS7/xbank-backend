package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationSinkGetTest extends AbstractServiceTest {

    @Spy
    private NotificationSinkRegistry notificationSinkRegistry;

    @InjectMocks
    private NotificationSinkGet notificationSinkGet;

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
    @DisplayName("should return a flux of notifications for the current user")
    void sinkGet_WhenValid_CreatesAndRemovesSink() {
        // given
        setUpContext(customer);
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
