package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class BankingTransactionTest extends AbstractServiceTest {

    private User customer;
    private BankingAccount account;
    private BankingTransaction transaction;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .admin()
            .build();

        account = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        transaction = BankingTransaction.createAccountTransaction(
            BankingTransactionType.DEPOSIT,
            account,
            BigDecimal.valueOf(100),
            "Test deposit transaction"
        );
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_WhenValidCustomerId_ReturnsTrue() {
        // given
        Long customerId = customer.getId();

        // when
        boolean result = transaction.isOwnedBy(customerId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the transaction does not belong to the given customer")
    void isOwnedBy_WhenInvalidCustomerId_ReturnsFalse() {
        // given
        Long otherCustomerId = 999L;

        // when
        boolean result = transaction.isOwnedBy(otherCustomerId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isOwnedBy returns false when the given customer ID is null")
    void isOwnedBy_WhenNullCustomerId_ReturnsFalse() {
        // when
        boolean result = transaction.isOwnedBy(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName(
        "assertOwnedBy throws BankingTransactionNotOwnerException when the customer does not own the transaction"
    )
    void assertOwnedBy_WhenInvalidCustomerId_ThrowsException() {
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
    void assertOwnedBy_WhenNullCustomerId_ThrowsException() {
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
    @DisplayName("Should confirm a transaction")
    void capture_WhenAuthorizedTransaction_ChangesStatusToCompleted() {
        // given
        BankingTransaction transaction = BankingTransactionTestFactory.aDepositTransaction()
            .withId(1L)
            .withAccount(account)
            .withAmount(BigDecimal.valueOf(120))
            .withStatus(BankingTransactionStatus.PENDING)
            .withPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED)
            .withType(BankingTransactionType.DEPOSIT)
            .withDescription("Deposit transaction")
            .build();

        // when
        transaction.capture();

        // then
        assertThat(transaction.getPaymentStatus())
            .isEqualTo(BankingTransactionPaymentStatus.CAPTURED);
    }

    @Test
    @DisplayName("Should reject a transaction")
    void failTransaction_WhenPendingTransaction_ChangesStatusToRejected() {
        // given
        BankingTransaction transaction = BankingTransaction.createAccountTransaction(
            BankingTransactionType.DEPOSIT,
            account,
            BigDecimal.valueOf(100),
            "Deposit transaction"
        );

        // when
        transaction.fail("failed");

        // then
        Assertions.assertThat(transaction.getStatus())
            .isEqualTo(BankingTransactionStatus.FAILED);
    }
}
