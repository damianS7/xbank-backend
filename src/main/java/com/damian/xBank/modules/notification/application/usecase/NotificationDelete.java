package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.service.NotificationDomainService;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationDelete {
    private static final Logger log = LoggerFactory.getLogger(NotificationDelete.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;
    private final NotificationDomainService notificationDomainService;
    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final Map<Long, Sinks.Many<NotificationEvent>> userSinks = new ConcurrentHashMap<>();

    public NotificationDelete(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository,
            NotificationDomainService notificationDomainService,
            UserAccountRepository userAccountRepository,
            CustomerRepository customerRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
        this.notificationDomainService = notificationDomainService;
        this.userAccountRepository = userAccountRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Delete all notifications for the current user.
     *
     * @param notificationIds List of notification ids to delete
     */
    @Transactional
    public void deleteNotifications(List<Long> notificationIds) {
        final User currentUser = authenticationContext.getCurrentUser();

        // delete selected notifications
        notificationRepository.deleteAllByIdInAndUser_Id(
                notificationIds,
                currentUser.getAccount().getId()
        );

        log.debug("Deleted {} notifications from user: {}", notificationIds.size(), currentUser.getId());
    }

    /**
     * Delete notification for the current user.
     *
     * @param id Notification id
     */
    @Transactional
    public void deleteNotification(Long id) {
        final User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotificationNotFoundException(id)
                );

        // Assert notification is owned by currentUser or throw
        notificationDomainService.deleteNotification(currentUser.getId(), notification);

        // delete notification
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", notification.getId());
    }
}