package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import com.damian.xBank.test.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class OutgoingTransferStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
        value = OutgoingTransferStatus.class,
        names = {"REJECTED", "CONFIRMED"}
    )
    @DisplayName("should return true when valid transition")
    void canTransitionTo_WhenAllowedStatus_ReturnsTrue(OutgoingTransferStatus toStatus) {
        assertThat(OutgoingTransferStatus.PENDING.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OutgoingTransferStatus.class, names = {"REJECTED", "COMPLETED"})
    @DisplayName("should return false when not allow any transitions from terminal states")
    void canTransitionTo_WhenNotAllowedStatus_ReturnsFalse(OutgoingTransferStatus fromStatus) {
        for (OutgoingTransferStatus targetStatus : OutgoingTransferStatus.values()) {
            assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}