package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.VERIFIED)
            .withRole(UserRole.ADMIN)
            .build();

        account = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        transaction = BankingTransaction.create(
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
    @DisplayName("assertOwnedBy returns the transaction when the customer owns it")
    void assertOwnedBy_WhenValidCustomerId_ReturnsTransaction() {
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
        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withId(1L)
            .withType(BankingTransactionType.DEPOSIT)
            .withAccount(account)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("Deposit transaction")
            .withStatus(BankingTransactionStatus.PENDING)
            .build();

        // when
        transaction.capture();

        // then
        Assertions.assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should reject a transaction")
    void failTransaction_WhenPendingTransaction_ChangesStatusToRejected() {
        // given
        BankingTransaction transaction = BankingTransaction.create(
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
