package com.damian.xBank.modules.notification.infrastructure.repository;

import com.damian.xBank.modules.notification.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByUserId(Long userId, Pageable pageable);


    void deleteAllByUser_Id(Long userId);

    void deleteAllByIdInAndUser_Id(List<Long> notificationIds, Long userId);
}

