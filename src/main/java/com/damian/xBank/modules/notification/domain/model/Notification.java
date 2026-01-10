package com.damian.xBank.modules.notification.domain.model;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;
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

    public Notification() {
        this.createdAt = Instant.now();
    }

    public Notification(User user) {
        this();
        this.user = user;
    }

    public static Notification create(User user) {
        return new Notification(user);
    }

    public Long getId() {
        return id;
    }

    public Notification setId(Long id) {
        this.id = id;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Notification setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public String toString() {
        return "Notification{" +
               "id=" + id +
               ",type=" + (getType() != null ? getType() : "null") +
               ",message=" + (getMessage() != null ? getMessage() : "null") +
               ",createdAt=" + createdAt +
               "}";
    }

    public User getOwner() {
        return user;
    }

    public Notification setOwner(User user) {
        this.user = user;
        return this;
    }

    public NotificationType getType() {
        return type;
    }

    public Notification setType(NotificationType type) {
        this.type = type;
        return this;
    }

    public String getMessage() {
        return metadata.get("message") != null ? metadata.get("message").toString() : null;
    }

    public Notification setMessage(String message) {
        metadata.put("message", message);
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Notification setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public boolean isOwnedBy(Long userId) {
        return getOwner().getId().equals(userId);
    }

    public void assertOwnedBy(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new NotificationNotOwnerException(getId(), userId);
        }
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public Notification setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
        return this;
    }
}
