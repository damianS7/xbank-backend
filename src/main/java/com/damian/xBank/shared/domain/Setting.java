package com.damian.xBank.shared.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "customer_settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> settings = new HashMap<>();

    public Setting() {

    }

    public Setting(Customer customer) {
        this.customer = customer;
    }

    public Setting(Customer customer, Map<String, Object> settings) {
        this(customer);
        this.settings = settings;
    }

    public Setting(User user, Map<String, Object> settings) {
        this(user.getCustomer(), settings);
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
               "customerId=" + getCustomer().getId() +
               "}";
    }

    public Customer getCustomer() {
        return customer;
    }

    public Setting setCustomer(Customer userAccount) {
        this.customer = userAccount;
        return this;
    }

    public boolean isOwner(Customer customer) {
        return this.customer.getId().equals(customer.getId());
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public String getSetting(String key) {
        return settings.get(key).toString();
    }
}
