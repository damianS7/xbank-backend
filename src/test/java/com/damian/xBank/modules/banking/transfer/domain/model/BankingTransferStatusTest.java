package com.damian.xBank.modules.banking.transfer.domain.model;

import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BankingTransferStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingTransferStatus.class,
            names = {"REJECTED", "CONFIRMED"}
    )
    @DisplayName("should return true when valid transition")
    void canTransitionTo_WhenAllowedStatus_ReturnsTrue(BankingTransferStatus toStatus) {
        assertThat(BankingTransferStatus.PENDING.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingTransferStatus.class, names = {"REJECTED", "CONFIRMED"})
    @DisplayName("should return false when not allow any transitions from terminal states")
    void canTransitionTo_WhenNotAllowedStatus_ReturnsFalse(BankingTransferStatus fromStatus) {
        for (BankingTransferStatus targetStatus : BankingTransferStatus.values()) {
            assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}