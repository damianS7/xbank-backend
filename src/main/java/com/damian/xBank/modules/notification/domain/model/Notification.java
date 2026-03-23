package com.damian.xBank.modules.notification.domain.model;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata = new HashMap<>();

    @Column
    private String templateKey;

    @Column
    private Instant createdAt;

    protected Notification() {
        this.createdAt = Instant.now();
    }

    Notification(
        Long id,
        User user,
        NotificationType type,
        Map<String, Object> metadata,
        String templateKey
    ) {
        this();
        this.id = id;
        this.user = user;
        this.type = type;
        this.metadata = metadata;
        this.templateKey = templateKey;
    }

    public static Notification create(
        User user,
        NotificationType type,
        Map<String, Object> metadata,
        String templateKey
    ) {
        return new Notification(null, user, type, metadata, templateKey);
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return user;
    }

    public NotificationType getType() {
        return type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean isOwnedBy(Long userId) {
        return getOwner().getId().equals(userId);
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void assertOwnedBy(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new NotificationNotOwnerException(getId(), userId);
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
               "id=" + id +
               ",type=" + (getType() != null ? getType() : "null") +
               ",metadata=" + (getMetadata() != null ? getMetadata() : "null") +
               ",templateKey=" + (getTemplateKey() != null ? getTemplateKey() : "null") +
               ",createdAt=" + createdAt +
               "}";
    }
}
