package com.damian.xBank.modules.setting.domain.model;

import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.infrastructure.persistence.converter.UserSettingsConverter;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    protected Setting() {
        // Constructor JPA
        this.settings = UserSettings.defaults();
    }

    Setting(User user, UserSettings settings) {
        this();
        this.user = user;
        this.settings = settings != null ? settings : UserSettings.defaults();
    }

    public static Setting create(User owner, UserSettings settings) {
        return new Setting(owner, settings);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean isOwnedBy(Long userId) {
        // compare account owner id with given customer id
        return Objects.equals(user.getId(), userId);
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public Setting assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new SettingNotOwnerException(getId(), userId);
        }

        return this;
    }

    @Override
    public String toString() {
        return "Setting {" +
               "id=" + id +
               "userId=" + user.getId() +
               "}";
    }
}
