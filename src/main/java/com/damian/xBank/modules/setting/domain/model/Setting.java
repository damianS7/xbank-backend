package com.damian.xBank.modules.setting.domain.model;

import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.infrastructure.persistence.converter.UserSettingsConverter;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.profile.domain.entity.UserProfile;
import com.damian.xBank.shared.security.UserPrincipal;
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
    private User user;

    @Convert(converter = UserSettingsConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private UserSettings settings;

    public Setting() {
    }

    public Setting(User user) {
        this.user = user;
    }

    public Setting(UserPrincipal owner) {
        this(owner.getUser());
    }

    public Setting(UserProfile owner) {
        this(owner.getUser());
    }

    public static Setting create(User owner) {
        return new Setting(owner);
    }

    public static Setting create(UserPrincipal owner) {
        return new Setting(owner);
    }

    public static Setting create(UserProfile owner) {
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

    public User getUserAccount() {
        return user;
    }

    public Setting setUserAccount(User user) {
        this.user = user;
        return this;
    }

    public boolean isOwnedBy(UserProfile customer) {
        return this.isOwnedBy(customer.getUser().getId());
    }

    public boolean isOwnedBy(UserPrincipal user) {
        return this.isOwnedBy(user.getUser().getId());
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
