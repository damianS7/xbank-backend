package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.notification.enums.NotificationType;
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
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private String message;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata = new HashMap<>();

    @Column
    private Instant createdAt;

    public Notification() {
        this.createdAt = Instant.now();
    }

    public Notification(UserAccount user) {
        this();
        this.user = user;
    }

    public static Notification create(UserAccount user) {
        return new Notification(user);
    }

    public static Notification create(Customer customer) {
        return new Notification(customer.getAccount());
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

    public UserAccount getOwner() {
        return user;
    }

    public Notification setOwner(UserAccount user) {
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
        return message;
    }

    public Notification setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Notification setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }
}
