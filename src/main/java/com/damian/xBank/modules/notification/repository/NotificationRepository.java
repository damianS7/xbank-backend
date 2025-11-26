package com.damian.xBank.modules.notification.repository;

import com.damian.xBank.shared.domain.Notification;
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

