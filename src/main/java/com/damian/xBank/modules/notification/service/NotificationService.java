package com.damian.xBank.modules.notification.service;

import com.damian.xBank.modules.notification.dto.NotificationEvent;
import com.damian.xBank.modules.notification.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.shared.domain.Notification;
import com.damian.xBank.shared.domain.User;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;
    private final Map<Long, Sinks.Many<NotificationEvent>> userSinks = new ConcurrentHashMap<>();

    public NotificationService(
            NotificationRepository notificationRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Get notifications for the current user.
     *
     * @param pageable pagination params
     * @return Page<Notification> a page of notifications
     */
    public Page<Notification> getNotifications(Pageable pageable) {
        User currentUser = AuthHelper.getCurrentUser();
        log.debug("Fetching notifications for user: {}", currentUser.getId());
        return notificationRepository.findAllByUserId(currentUser.getId(), pageable);
    }

    /**
     * Delete all notifications for the current user.
     */
    @Transactional
    public void deleteNotifications() {
        User currentUser = AuthHelper.getCurrentUser();
        // delete all notifications
        notificationRepository.deleteAllByUser_Id(currentUser.getId());
        log.debug("Deleted all notifications from user: {}", currentUser.getId());
    }

    /**
     * Delete notification for the current user.
     *
     * @param id Notification id
     */
    @Transactional
    public void deleteNotification(Long id) {
        User currentUser = AuthHelper.getCurrentUser();

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
        User currentUser = AuthHelper.getCurrentUser();

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
    public void publishNotification(NotificationEvent notificationEvent) {
        User currentUser = AuthHelper.getCurrentUser();
        log.debug(
                "Publishing Notification ({}) to user: {}",
                notificationEvent.type(),
                notificationEvent.recipientId()
        );

        // if the receiverId is the same as senderId then do nothing
        // this is to prevent sending notifications to oneself
        // for example when a user likes or comment their own post
        if (currentUser.getId().equals(notificationEvent.recipientId())) {
            log.debug("Recipient is the same user. No need to notify.");
            return;
        }

        // find recipient user who will receive the notification
        UserAccount recipient = userAccountRepository
                .findById(notificationEvent.recipientId())
                .orElseThrow(() -> {
                    log.warn(
                            "Notification failed: recipient: {} not found.",
                            notificationEvent.recipientId()
                    );
                    return new UserAccountNotFoundException(
                            Exceptions.USER.ACCOUNT.NOT_FOUND,
                            notificationEvent.recipientId()
                    );
                });

        // create and save notification to the database
        Notification notification = Notification
                .create(recipient)
                .setMessage(notificationEvent.message())
                .setMetadata(notificationEvent.metadata())
                .setType(notificationEvent.type());
        notificationRepository.save(notification);

        log.debug(
                "Notification ({}) to user: {} stored on db.",
                notificationEvent.type(),
                notificationEvent.recipientId()
        );

        // emit event to the recipient if connected
        var sink = userSinks.get(notificationEvent.recipientId());
        if (sink != null) {
            sink.tryEmitNext(notificationEvent);
            log.debug("Notification sent on real time to: {}", notificationEvent.recipientId());
        }
    }
}