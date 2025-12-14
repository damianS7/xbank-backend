package com.damian.xBank.modules.banking.transaction.domain.enums;

import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusNotAllowedException;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BankingTransactionStatusTest extends AbstractServiceTest {
    @Test
    @DisplayName("Should validate any transitions from PENDING")
    void shouldValidateTransitionsFromPending() {
        for (BankingTransactionStatus status : BankingTransactionStatus.values()) {

            if (status == BankingTransactionStatus.PENDING) {
                continue;
            }
            
            assertThatNoException().isThrownBy(() ->
                    BankingTransactionStatus.PENDING.validateTransition(status)
            );
        }
    }

    @ParameterizedTest
    @EnumSource(value = BankingTransactionStatus.class, names = {"FAILED", "REJECTED", "COMPLETED"})
    @DisplayName("Should not allow any transitions from terminal states")
    void shouldNotAllowTransitionsFromTerminalStates(BankingTransactionStatus fromStatus) {
        for (BankingTransactionStatus targetStatus : BankingTransactionStatus.values()) {
            assertThatThrownBy(() ->
                    fromStatus.validateTransition(targetStatus)
            )
                    .isInstanceOf(BankingTransactionStatusNotAllowedException.class)
                    .hasMessageContaining(fromStatus.name());
        }
    }
}