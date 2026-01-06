package com.damian.xBank.modules.banking.transfer.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
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

        fromAccount = BankingAccount.create(fromCustomer)
                                    .setCurrency(BankingAccountCurrency.EUR);

        toAccount = BankingAccount.create(toCustomer)
                                  .setId(1L)
                                  .setCurrency(BankingAccountCurrency.EUR);

        transfer = BankingTransfer.create(fromAccount, toAccount, BigDecimal.ZERO)
                                  .setId(2L);

        BankingTransaction fromTx = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_TO,
                        fromAccount,
                        BigDecimal.ZERO
                );

        BankingTransaction toTx = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        BigDecimal.ZERO
                );

        transfer.addTransaction(fromTx);
        transfer.addTransaction(toTx);
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
        transfer.setStatus(BankingTransferStatus.PENDING);

        // when
        BankingTransfer result = transfer.setStatus(BankingTransferStatus.CONFIRMED);

        // then
        assertThat(result).isSameAs(transfer);
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.CONFIRMED);
    }

    @Test
    @DisplayName("setStatus same status does nothing")
    void setStatus_WhenSameStatus_DoesNothing() {
        // given
        transfer.setStatus(BankingTransferStatus.PENDING);

        // when
        BankingTransfer result = transfer.setStatus(BankingTransferStatus.PENDING);

        // then
        assertThat(result).isSameAs(transfer);
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.PENDING);
    }

    @Test
    @DisplayName("setStatus invalid transition throws BankingTransferStatusTransitionException")
    void setStatus_WhenInvalidTransition_ThrowsException() {
        // given
        transfer.setStatus(BankingTransferStatus.CONFIRMED);

        // when / then
        BankingTransferStatusTransitionException exception = assertThrows(
                BankingTransferStatusTransitionException.class,
                () -> transfer.setStatus(BankingTransferStatus.PENDING)
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
    @DisplayName("addTransaction should set transfer on transaction")
    void addTransaction_WhenValid_SetTransferOnTransaction() {
        // given
        // when
        BankingTransaction testTx = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        BigDecimal.ZERO
                );

        transfer.addTransaction(testTx);

        // then
        assertThat(testTx.getTransfer())
                .isNotNull()
                .extracting(
                        BankingTransfer::getId
                ).isEqualTo(
                        transfer.getId()
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
        transfer.reject();

        // then
        assertThat(transfer.getStatus()).isEqualTo(BankingTransferStatus.REJECTED);
        assertThat(transfer.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("assertCurrenciesMatch should return transfer when currencies are equal")
    void assertCurrenciesMatch_WhenCurrenciesAreEqual_ReturnsTransfer() {
        // given
        fromAccount.setCurrency(BankingAccountCurrency.EUR);

        toAccount.setCurrency(BankingAccountCurrency.EUR);

        // when
        BankingTransfer result = transfer.assertCurrenciesMatch();

        // then
        assertThat(result).isSameAs(transfer);
    }

    @Test
    @DisplayName("assertCurrenciesMatch should return exception when currencies are not equal")
    void assertCurrenciesMatch_WhenCurrenciesAreDifferent_ThrowsException() {
        // given
        fromAccount.setCurrency(BankingAccountCurrency.USD);

        toAccount.setCurrency(BankingAccountCurrency.EUR);

        // when
        BankingTransferCurrencyMismatchException exception = assertThrows(
                BankingTransferCurrencyMismatchException.class,
                transfer::assertCurrenciesMatch
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY);
    }

    @Test
    @DisplayName("assertDifferentAccounts should return transfer when accounts are different")
    void assertDifferentAccounts_WhenAccountAreDifferent_ReturnsTransfer() {
        // given
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);

        // when
        BankingTransfer result = transfer.assertDifferentAccounts();

        // then
        assertThat(result).isSameAs(transfer);
    }

    @Test
    @DisplayName("assertDifferentAccounts should return exception when accounts are equal")
    void assertDifferentAccounts_WhenAccountsAreEqual_ThrowsException() {
        // given
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(fromAccount);

        // when
        BankingTransferSameAccountException exception = assertThrows(
                BankingTransferSameAccountException.class,
                transfer::assertDifferentAccounts
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT);
    }

}
