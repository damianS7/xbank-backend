package com.damian.xBank.modules.banking.transaction.domain.enums;

import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BankingTransactionStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingTransactionStatus.class,
            names = {"FAILED", "REJECTED", "COMPLETED"}
    )
    @DisplayName("Should validate transitions from PENDING_ACTIVATION")
    void shouldValidateTransitionsFromPendingActivation(BankingTransactionStatus toStatus) {
        assertThatCode(() ->
                BankingTransactionStatus.PENDING.validateTransition(toStatus)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = BankingTransactionStatus.class, names = {"FAILED", "REJECTED", "COMPLETED"})
    @DisplayName("Should not validate any transitions from terminal states")
    void shouldNotValidateTransitionsFromTerminalStates(BankingTransactionStatus fromStatus) {
        for (BankingTransactionStatus targetStatus : BankingTransactionStatus.values()) {
            assertThatThrownBy(() ->
                    fromStatus.validateTransition(targetStatus)
            )
                    .isInstanceOf(BankingTransactionStatusTransitionException.class)
                    .hasMessageContaining(fromStatus.name());
        }
    }
}