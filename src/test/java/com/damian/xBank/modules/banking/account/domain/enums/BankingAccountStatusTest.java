package com.damian.xBank.modules.banking.account.domain.enums;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BankingAccountStatusTest extends AbstractServiceTest {

    @ParameterizedTest
    @EnumSource(
            value = BankingAccountStatus.class,
            names = {"ACTIVE", "CLOSED"}
    )
    @DisplayName("Should validate transitions from PENDING_ACTIVATION")
    void shouldValidateTransitionsFromPendingActivation(BankingAccountStatus toStatus) {
        assertThatCode(() ->
                BankingAccountStatus.PENDING_ACTIVATION.validateTransition(toStatus)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = BankingAccountStatus.class, names = {"CLOSED", "SUSPENDED"})
    @DisplayName("Should not allow any transitions from terminal states")
    void shouldNotAllowTransitionsFromTerminalStates(BankingAccountStatus fromStatus) {
        for (BankingAccountStatus targetStatus : BankingAccountStatus.values()) {
            assertThatThrownBy(() ->
                    fromStatus.validateTransition(targetStatus)
            )
                    .isInstanceOf(BankingAccountStatusTransitionException.class)
                    .hasMessage(Exceptions.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS);
        }
    }
}