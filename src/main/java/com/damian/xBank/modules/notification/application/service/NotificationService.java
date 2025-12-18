package com.damian.xBank.modules.notification.application.service;

import com.damian.xBank.modules.notification.domain.entity.Notification;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.infra.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.customer.infra.repository.CustomerRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final Map<Long, Sinks.Many<NotificationEvent>> userSinks = new ConcurrentHashMap<>();

    public NotificationService(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository,
            UserAccountRepository userAccountRepository,
            CustomerRepository customerRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Get notifications for the current user.
     *
     * @param pageable pagination params
     * @return Page<Notification> a page of notifications
     */
    public Page<Notification> getNotifications(Pageable pageable) {
        User currentUser = authenticationContext.getCurrentUser();
        log.debug("Fetching notifications for user: {}", currentUser.getId());
        return notificationRepository.findAllByUserId(currentUser.getId(), pageable);
    }

    /**
     * Delete all notifications for the current user.
     *
     * @param notificationIds List of notification ids to delete
     */
    @Transactional
    public void deleteNotifications(List<Long> notificationIds) {
        User currentUser = authenticationContext.getCurrentUser();

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
        User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotificationNotFoundException(id)
                );

        // TODO check ownership
        if (!notification.getOwner().getId().equals(currentUser.getAccount().getId())) {
            // throw
        }

        // delete notification
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", notification.getId());
    }

    /**
     * Get notifications for the current user as a Flux stream.
     * The stream will be closed when the client disconnects.
     *
     * @return Flux<NotificationEvent> a stream of notifications
     */
    public Flux<NotificationEvent> getNotificationsForUser() {
        User currentUser = authenticationContext.getCurrentUser();

        // create a sink for the user if not exists
        Sinks.Many<NotificationEvent> sink = userSinks.computeIfAbsent(
                currentUser.getId(),
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

        // remove when disconnect
        return sink.asFlux().doOnCancel(() -> {
            userSinks.remove(currentUser.getId());
        });
    }

    /**
     * Publish a notification event to the recipient.
     *
     * @param notificationEvent the notification event
     */
    public void publish(NotificationEvent notificationEvent) {
        // find recipient user who will receive the notification
        UserAccount recipient = userAccountRepository
                .findById(notificationEvent.recipientId())
                .orElseThrow(() -> {
                    log.warn(
                            "Notification failed: recipient: {} not found.",
                            notificationEvent.recipientId()
                    );
                    return new UserAccountNotFoundException(notificationEvent.recipientId());
                });

        // create and save notification to the database
        Notification notification = Notification
                .create(recipient)
                .setMetadata(notificationEvent.metadata())
                .setType(notificationEvent.type());
        notificationRepository.save(notification);

        // emit event to the recipient if connected
        var sink = userSinks.get(notificationEvent.recipientId());
        if (sink != null) {
            sink.tryEmitNext(notificationEvent);
        }

        log.debug(
                "Notification ({}) sent to user: {}",
                notificationEvent.type(),
                notificationEvent.recipientId()
        );
    }
}