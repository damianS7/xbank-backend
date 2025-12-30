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
    @DisplayName("Should validate transitions from PENDING_ACTIVATION")
    void canTransitionTo_AllowedStatus_ReturnsTrue(BankingAccountStatus toStatus) {
        assertThat(BankingAccountStatus.PENDING_ACTIVATION.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingAccountStatus.class, names = {"CLOSED", "SUSPENDED"})
    @DisplayName("Should not allow any transitions from terminal states")
    void canTransitionTo_NotAllowedStatus_ReturnsFalse(BankingAccountStatus fromStatus) {
        for (BankingAccountStatus targetStatus : BankingAccountStatus.values()) {
            Assertions.assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}