package com.damian.xBank.shared.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> settings = new HashMap<>();

    public Setting() {

    }

    public Setting(UserAccount userAccount) {
        this.user = userAccount;
    }

    public Setting(User owner) {
        this(owner.getAccount());
    }

    public Setting(Customer owner) {
        this(owner.getAccount());
    }

    public static Setting create(User owner) {
        return new Setting(owner);
    }

    public static Setting create(Customer owner) {
        return new Setting(owner);
    }

    public static Setting create() {
        return new Setting();
    }

    public Long getId() {
        return id;
    }

    public Setting setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Setting {" +
               "id=" + id +
               "userId=" + user.getId() +
               "}";
    }

    public UserAccount getUserAccount() {
        return user;
    }

    public Setting setUserAccount(UserAccount userAccount) {
        this.user = userAccount;
        return this;
    }

    public boolean isOwner(Customer customer) {
        return this.isOwner(customer.getAccount());
    }

    public boolean isOwner(User user) {
        return this.isOwner(user.getAccount());
    }

    public boolean isOwner(UserAccount user) {
        return this.user.getId().equals(user.getId());
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public Setting setSettings(Map<String, Object> settings) {
        this.settings = settings;
        return this;
    }

    public String getSetting(String key) {
        return settings.get(key).toString();
    }
}
