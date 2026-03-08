package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotifications;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotificationsCommand;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

public class DeleteNotificationsTest extends AbstractServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DeleteNotifications deleteNotifications;

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
    @DisplayName("should delete notifications")
    void deleteNotifications_ValidRequest_DeleteNotifications() {
        // given
        setUpContext(customer);

        Notification notification = Notification.create(customer)
            .setId(1L);

        DeleteNotificationsCommand command = new DeleteNotificationsCommand(
            List.of(notification.getId())
        );

        // when
        deleteNotifications.execute(command);

        // then
        verify(notificationRepository).deleteAllByIdInAndUser_Id(anyList(), anyLong());
    }
}
