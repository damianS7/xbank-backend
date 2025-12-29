package com.damian.xBank.modules.setting.domain.model;

import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.infrastructure.persistence.converter.UserSettingsConverter;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

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

    public static Setting create(UserAccount owner) {
        return new Setting(owner);
    }

    public static Setting create(User owner) {
        return new Setting(owner);
    }

    public static Setting create(Customer owner) {
        return new Setting(owner);
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

    public boolean isOwnedBy(Customer customer) {
        return this.isOwnedBy(customer.getAccount().getId());
    }

    public boolean isOwnedBy(User user) {
        return this.isOwnedBy(user.getAccount().getId());
    }

    public boolean isOwnedBy(Long userId) {
        // compare account owner id with given customer id
        return Objects.equals(user.getId(), userId);
    }

    public Setting assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new SettingNotOwnerException(getId(), userId);
        }

        return this;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public Setting setSettings(UserSettings settings) {
        this.settings = settings;
        return this;
    }
}
