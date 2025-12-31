package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.shared.AbstractServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BankingAccountStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingAccountStatus.class,
            names = {"ACTIVE", "CLOSED"}
    )
    @DisplayName("Should returns true when transition goes from PENDING to a valid status")
    void canTransitionTo_WhenAllowedStatus_ReturnsTrue(BankingAccountStatus toStatus) {
        assertThat(BankingAccountStatus.PENDING_ACTIVATION.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingAccountStatus.class, names = {"CLOSED", "SUSPENDED"})
    @DisplayName("Should returns false when transitions goes from terminal states")
    void canTransitionTo_WhenNotAllowedStatus_ReturnsFalse(BankingAccountStatus fromStatus) {
        for (BankingAccountStatus targetStatus : BankingAccountStatus.values()) {
            Assertions.assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}