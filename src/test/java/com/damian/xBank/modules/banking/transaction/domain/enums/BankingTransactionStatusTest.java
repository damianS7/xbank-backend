package com.damian.xBank.modules.banking.transaction.domain.enums;

import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BankingTransactionStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingTransactionStatus.class,
            names = {"FAILED", "REJECTED", "COMPLETED"}
    )
    @DisplayName("Should validate transitions from PENDING_ACTIVATION")
    void canTransationTo_AllowedStatus_ReturnsTrue(BankingTransactionStatus toStatus) {
        assertThat(BankingTransactionStatus.PENDING.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingTransactionStatus.class, names = {"FAILED", "REJECTED", "COMPLETED"})
    @DisplayName("Should not validate any transitions from terminal states")
    void canTransationTo_NotAllowedStatus_ReturnsFalse(BankingTransactionStatus fromStatus) {
        for (BankingTransactionStatus targetStatus : BankingTransactionStatus.values()) {
            assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}