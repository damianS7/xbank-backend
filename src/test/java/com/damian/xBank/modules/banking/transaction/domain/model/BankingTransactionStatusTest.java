package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BankingTransactionStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingTransactionStatus.class,
            names = {"AUTHORIZED", "DECLINED"}
    )
    @DisplayName("should return true when validate transitions from PENDING_ACTIVATION")
    void canTransactionTo_WhenAllowedStatus_ReturnsTrue(BankingTransactionStatus toStatus) {
        assertThat(BankingTransactionStatus.PENDING.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingTransactionStatus.class, names = {"DECLINED", "CAPTURED"})
    @DisplayName("should return false when not validate any transitions from terminal states")
    void canTransactionTo_WhenNotAllowedStatus_ReturnsFalse(BankingTransactionStatus fromStatus) {
        for (BankingTransactionStatus targetStatus : BankingTransactionStatus.values()) {
            assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}