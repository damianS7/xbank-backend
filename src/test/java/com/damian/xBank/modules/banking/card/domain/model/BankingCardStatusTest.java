package com.damian.xBank.modules.banking.card.domain.model;

import com.damian.xBank.shared.AbstractServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class BankingCardStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingCardStatus.class,
            names = {"ACTIVE", "DISABLED", "EXPIRED"}
    )
    @DisplayName("should return true when validate transitions from PENDING_ACTIVATION")
    void shouldValidateTransitionsFromPendingActivation(BankingCardStatus toStatus) {
        Assertions.assertThat(BankingCardStatus.PENDING_ACTIVATION.canTransitionTo(toStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BankingCardStatus.class, names = {"EXPIRED", "DISABLED"})
    @DisplayName("should return false when validate not allowed transitions")
    void canTransitionTo_WhenNotAllowedStatus_ReturnsFalse(BankingCardStatus fromStatus) {
        for (BankingCardStatus targetStatus : BankingCardStatus.values()) {
            Assertions.assertThat(fromStatus.canTransitionTo(targetStatus)).isFalse();
        }
    }
}