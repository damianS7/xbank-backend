package com.damian.xBank.modules.banking.transaction.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class BankingTransactionTest {

    private Customer customer;
    private BankingAccount account;
    private BankingTransaction transaction;

    @BeforeEach
    void setUp() {
        customer = Customer.create().setId(1L);
        account = BankingAccount.create(customer);
        transaction = BankingTransaction
                .create(
                        BankingTransactionType.DEPOSIT,
                        account,
                        BigDecimal.valueOf(100)
                );
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_ValidCustomerId_ReturnsTrue() {
        // given
        Long customerId = customer.getId();

        // when
        boolean result = transaction.isOwnedBy(customerId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_InvalidCustomerId_ReturnsFalse() {
        // given
        Long otherCustomerId = 999L;

        // when
        boolean result = transaction.isOwnedBy(otherCustomerId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the given customer ID is null")
    void isOwnedBy_NullCustomerId_ReturnsFalse() {
        // when
        boolean result = transaction.isOwnedBy(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("assertOwnedBy returns the transaction when the customer owns it")
    void assertOwnedBy_ValidCustomerId_ReturnsTransaction() {
        // given
        Long customerId = customer.getId();

        // when
        BankingTransaction result = transaction.assertOwnedBy(customerId);

        // then
        assertThat(result).isSameAs(transaction);
    }

    @Test
    @DisplayName(
            "assertOwnedBy throws BankingTransactionNotOwnerException when the customer does not own the transaction"
    )
    void assertOwnedBy_InvalidCustomerId_ThrowsException() {
        // given
        Long otherCustomerId = 999L;

        // when / then
        BankingTransactionNotOwnerException exception = assertThrows(
                BankingTransactionNotOwnerException.class,
                () -> transaction.assertOwnedBy(otherCustomerId)
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }

    @Test
    @DisplayName("assertOwnedBy throws BankingTransactionNotOwnerException when the given customer ID is null")
    void assertOwnedBy_NullCustomerId_ThrowsException() {
        // when / then
        BankingTransactionNotOwnerException exception = assertThrows(
                BankingTransactionNotOwnerException.class,
                () -> transaction.assertOwnedBy(null)
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }

    @Test
    @DisplayName("setBankingAccount sets the account and assigns the customer from the account")
    void setBankingAccount_AssignsAccountAndCustomer_ReturnsTransaction() {
        // given
        BankingAccount account = BankingAccount.create(customer);

        // when
        BankingTransaction result = transaction.setBankingAccount(account);

        // then
        assertThat(result).isSameAs(transaction);
        assertThat(transaction.getBankingAccount()).isSameAs(account);
        assertThat(transaction.getCustomer()).isSameAs(customer);
    }

    @Test
    @DisplayName("setBankingCard sets the card and assigns the customer from the card")
    void setBankingCard_AssignsCardAndCustomer_ReturnsTransaction() {
        // given
        BankingAccount account = BankingAccount.create(customer);

        BankingCard card = BankingCard.create(account);

        // when
        BankingTransaction result = transaction.setBankingCard(card);

        // then
        assertThat(result).isSameAs(transaction);
        assertThat(transaction.getBankingCard()).isSameAs(card);
        assertThat(transaction.getCustomer()).isSameAs(customer);
    }

    @Test
    @DisplayName("setStatus set status COMPLETED")
    void setStatus_ValidTransition_UpdatesStatus() {
        // given
        transaction.setStatus(BankingTransactionStatus.PENDING);

        // when
        BankingTransaction result = transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // then
        assertThat(result).isSameAs(transaction);
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("setStatus same status does nothing")
    void setStatus_SameStatus_DoesNothing() {
        // given
        transaction.setStatus(BankingTransactionStatus.PENDING);

        // when
        BankingTransaction result = transaction.setStatus(BankingTransactionStatus.PENDING);

        // then
        assertThat(result).isSameAs(transaction);
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.PENDING);
    }

    @Test
    @DisplayName("setStatus invalid transition throws BankingTransactionStatusTransitionException")
    void setStatus_InvalidTransition_ThrowsException() {
        // given
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // when / then
        BankingTransactionStatusTransitionException exception = assertThrows(
                BankingTransactionStatusTransitionException.class,
                () -> transaction.setStatus(BankingTransactionStatus.PENDING)
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_INVALID_TRANSITION_STATUS);
    }

}
