package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserNotifications;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserNotificationsQuery;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.utils.UserTestBuilder;
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

public class GetCurrentUserNotificationsTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private GetCurrentUserNotifications getCurrentUserNotifications;

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
    @DisplayName("should return notifications for the current user")
    void getAllNotifications_WhenValid_ReturnsCustomerNotifications() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(
            List.of(
                Notification.create(customer),
                Notification.create(customer),
                Notification.create(customer)
            ),
            pageable,
            3
        );

        GetCurrentUserNotificationsQuery query = new GetCurrentUserNotificationsQuery(pageable);

        // when
        when(notificationRepository.findAllByUserId(customer.getId(), pageable))
            .thenReturn(page);

        PageResult<NotificationResult> result = getCurrentUserNotifications.execute(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
        verify(notificationRepository).findAllByUserId(customer.getId(), pageable);
    }
}
