package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationGetTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

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
    @DisplayName("should return notifications for the current user")
    void getAllNotifications_WhenValid_ReturnsCustomerNotifications() {
        // given
        setUpContext(customer.getAccount());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(
                List.of(
                        Notification.create(customer),
                        Notification.create(customer),
                        Notification.create(customer)
                )
        );

        // when
        when(notificationRepository.findAllByUserId(customer.getId(), pageable)).thenReturn(page);

        Page<Notification> result = notificationGet.execute(pageable);

        // then
        assertThat(result).isEqualTo(page);
        assertThat(result.getTotalElements()).isEqualTo(page.getTotalElements());
        verify(notificationRepository).findAllByUserId(customer.getId(), pageable);
    }
}
