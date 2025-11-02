package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.setting.UserSettings;
import com.damian.xBank.modules.setting.UserSettingsConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @Convert(converter = UserSettingsConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private UserSettings settings;

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

    public UserSettings getSettings() {
        return settings;
    }

    public Setting setSettings(UserSettings settings) {
        this.settings = settings;
        return this;
    }
}
