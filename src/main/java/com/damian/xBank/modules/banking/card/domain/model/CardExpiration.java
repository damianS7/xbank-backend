package com.damian.xBank.modules.banking.card.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.YearMonth;

@Embeddable
public class CardExpiration {

    @Column(name = "expiration_year", nullable = false)
    private int year;

    @Column(name = "expiration_month", nullable = false)
    private int month;

    protected CardExpiration() {
    }

    public CardExpiration(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month");
        }
        this.year = year;
        this.month = month;
    }

    public static CardExpiration defaultExpiration() {
        YearMonth expiration = YearMonth.now().plusYears(3);
        return new CardExpiration(
            expiration.getYear(),
            expiration.getMonthValue()
        );
    }

    public static CardExpiration of(int year, int month) {
        return new CardExpiration(year, month);
    }

    public YearMonth toYearMonth() {
        return YearMonth.of(year, month);
    }

    public int getMonth() {
        return month;
    }
    
    public int getYear() {
        return year;
    }

    public boolean isExpired() {
        return toYearMonth().isBefore(YearMonth.now());
    }
}