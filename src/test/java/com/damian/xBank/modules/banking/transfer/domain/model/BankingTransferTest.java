package com.damian.xBank.modules.banking.transfer.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferStatusTransitionException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BankingTransferTest {

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;
    private BankingTransfer transfer;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.aCustomer()
            .withId(1L)
            .build();
        toCustomer = UserTestBuilder.aCustomer()
            .withId(2L)
            .build();

        fromAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456781012")
            .build();

        transfer = BankingTransferTestBuilder.builder()
            .withId(2L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
            .withAmount(BigDecimal.ZERO)
            .withDescription("a gift!")
            .build();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_WhenValidCustomerId_ReturnsTrue() {
        // given
        Long customerId = fromCustomer.getId();

        // when
        boolean result = transfer.isOwnedBy(customerId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_WhenInvalidCustomerId_ReturnsFalse() {
        // given
        Long otherCustomerId = 999L;

        // when
        boolean result = transfer.isOwnedBy(otherCustomerId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the given customer ID is null")
    void isOwnedBy_WhenNullCustomerId_ReturnsFalse() {
        // when
        boolean result = transfer.isOwnedBy(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("assertOwnedBy returns the transaction when the customer owns it")
    void assertOwnedBy_WhenValidCustomerId_ReturnsTransfer() {
        // given
        Long customerId = fromCustomer.getId();

        // when
        BankingTransfer result = transfer.assertOwnedBy(customerId);

        // then
        assertThat(result).isSameAs(transfer);
    }

    @Test
    @DisplayName(
        "assertOwnedBy throws BankingTransferNotOwnerException when the customer does not own the transaction"
    )
    void assertOwnedBy_WhenInvalidCustomerId_ThrowsException() {
        // given
        Long otherCustomerId = 999L;

        // when / then
        BankingTransferNotOwnerException exception = assertThrows(
            BankingTransferNotOwnerException.class,
            () -> transfer.assertOwnedBy(otherCustomerId)
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSFER_NOT_OWNER);
    }

    @Test
    @DisplayName("assertOwnedBy throws BankingTransferNotOwnerException when the given customer ID is null")
    void assertOwnedBy_WhenNullCustomerId_ThrowsException() {
        // when / then
        BankingTransferNotOwnerException exception = assertThrows(
            BankingTransferNotOwnerException.class,
            () -> transfer.assertOwnedBy(null)
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSFER_NOT_OWNER);
    }

    @Test
    @DisplayName("setStatus set status COMPLETED")
    void setStatus_WhenValidTransition_UpdatesStatus() {
        // given
        transfer.confirm();

        // when
        transfer.authorize("1234");

        // then
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.AUTHORIZED);
    }

    @Test
    @DisplayName("setStatus invalid transition throws BankingTransferStatusTransitionException")
    void setStatus_WhenInvalidTransition_ThrowsException() {
        // given
        // when / then
        BankingTransferStatusTransitionException exception = assertThrows(
            BankingTransferStatusTransitionException.class,
            () -> transfer.complete()
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSFER_INVALID_TRANSITION_STATUS);
    }

    @Test
    @DisplayName("getFromTransaction should returns sender transaction")
    void getFromTransaction_WhenValid_ReturnsTransaction() {
        // given
        // when
        BankingTransaction tx = transfer.getFromTransaction();

        // then
        assertThat(tx)
            .isNotNull()
            .extracting(
                BankingTransaction::getType,
                BankingTransaction::getBankingAccount
            ).containsExactly(
                BankingTransactionType.TRANSFER_TO,
                fromAccount
            );

    }

    @Test
    @DisplayName("getToTransaction should returns receiver transaction")
    void getToTransaction_WhenValid_ReturnsTransaction() {
        // given
        // when
        BankingTransaction tx = transfer.getToTransaction();

        // then
        assertThat(tx)
            .isNotNull()
            .extracting(
                BankingTransaction::getType,
                BankingTransaction::getBankingAccount
            ).containsExactly(
                BankingTransactionType.TRANSFER_FROM,
                toAccount
            );

    }

    @Test
    @DisplayName("should confirms transfer and set CONFIRMED status")
    void confirm_WhenValid_ConfirmsTransfer() {
        // given
        // when
        transfer.confirm();

        // then
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.CONFIRMED);
        assertThat(transfer.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should reject transfer and set REJECTED status")
    void reject_WhenValid_RejectsTransfer() {
        // given
        // when
        transfer.reject("rejected");

        // then
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.REJECTED);
        assertThat(transfer.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("assertCurrenciesMatch should return transfer when currencies are equal")
    void assertCurrenciesMatch_WhenCurrenciesAreEqual_ReturnsTransfer() {
        // given
        fromAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456783012")
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        // when
        assertDoesNotThrow(() -> transfer.assertCurrenciesMatch());
        // then
    }

    @Test
    @DisplayName("assertCurrenciesMatch should return exception when currencies are not equal")
    void assertCurrenciesMatch_WhenCurrenciesAreDifferent_ThrowsException() {
        // given
        BankingAccount fromAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.USD)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456783012")
            .build();

        BankingAccount toAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES9000567890123456789012")
            .build();

        // when
        BankingTransferCurrencyMismatchException exception = assertThrows(
            BankingTransferCurrencyMismatchException.class,
            () -> BankingTransfer.create(
                fromAccount, toAccount, null, BigDecimal.valueOf(100), "")
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY);
    }

    @Test
    @DisplayName("assertDifferentAccounts should return transfer when accounts are different")
    void assertDifferentAccounts_WhenAccountAreDifferent_ReturnsTransfer() {
        // given
        BankingTransfer transfer = BankingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        // when
        BankingTransfer result = transfer.assertDifferentAccounts();

        // then
        assertThat(result).isSameAs(transfer);
    }

    @Test
    @DisplayName("throws exception when accounts are equal")
    void create_WhenAccountsAreEqual_ThrowsException() {
        // given
        // when
        BankingTransferSameAccountException exception = assertThrows(
            BankingTransferSameAccountException.class,
            () -> BankingTransfer.create(
                fromAccount,
                fromAccount,
                null,
                BigDecimal.valueOf(100),
                "a gift!"
            )
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT);
    }

}
