package com.damian.xBank.modules.banking.card.domain.enums;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardStatusTransitionException;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BankingCardStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingCardStatus.class,
            names = {"ACTIVE", "DISABLED", "EXPIRED"}
    )
    @DisplayName("Should validate transitions from PENDING_ACTIVATION")
    void shouldValidateTransitionsFromPendingActivation(BankingCardStatus toStatus) {
        assertThatCode(() ->
                BankingCardStatus.PENDING_ACTIVATION.validateTransition(toStatus)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = BankingCardStatus.class, names = {"EXPIRED", "DISABLED"})
    @DisplayName("Should not allow any transitions from terminal states")
    void shouldNotAllowTransitionsFromTerminalStates(BankingCardStatus fromStatus) {
        for (BankingCardStatus targetStatus : BankingCardStatus.values()) {
            assertThatThrownBy(() ->
                    fromStatus.validateTransition(targetStatus)
            )
                    .isInstanceOf(BankingCardStatusTransitionException.class)
                    .hasMessage(ErrorCodes.BANKING_CARD_INVALID_TRANSITION_STATUS);
        }
    }
}