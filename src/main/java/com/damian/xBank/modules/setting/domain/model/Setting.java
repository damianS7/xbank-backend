package com.damian.xBank.modules.setting.domain.model;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Setting create() {
        return new Setting(null, null, UserSettings.defaults());
    }

    public void updateSettings(UserSettings settings) {
        this.settings = settings;
    }

    public void assignOwner(User owner) {
        this.user = owner;
    }

    @Override
    public String toString() {
        return "Setting{" +
               "id=" + id +
               ", settings=" + settings +
               '}';
    }
}
