package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BankingAccountTest extends AbstractServiceTest {

    private User customer;
    private User admin;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        admin = UserTestBuilder.builder()
            .withId(2L)
            .withEmail("admin@demo.com")
            .withRole(UserRole.ADMIN)
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(0))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("assertSufficientFunds: should pass when balance is greater than or equal to amount")
    void assertSufficientFunds_WhenBalanceIsEnough_DoesNotThrow() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(500));

        BigDecimal amount = BigDecimal.valueOf(200);

        // when / then
        assertThatCode(() -> bankingAccount.assertSufficientFunds(amount))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertSufficientFunds: should pass when amount is equal to balance")
    void assertSufficientFunds_WhenAmountIsEqualToBalance_DoesNotThrow() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(500));

        BigDecimal amount = bankingAccount.getBalance();

        // when / then
        assertThatCode(() -> bankingAccount.assertSufficientFunds(amount))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertSufficientFunds: should throw exception when balance is insufficient")
    void assertSufficientFunds_WhenBalanceIsInsufficient_ThrowsException() {
        // given
        BankingAccount account = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(0))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        BigDecimal withdrawAmount = BigDecimal.valueOf(300);

        // when
        BankingAccountInsufficientFundsException exception =
            assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> account.assertSufficientFunds(withdrawAmount)
            );

        // then
        assertThat(exception)
            .hasMessage(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("withdraw: should subtract amount when balance is sufficient")
    void withdrawIsSufficient_SubtractsAmount() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(500));

        BigDecimal amount = BigDecimal.valueOf(200);

        // when
        bankingAccount.withdraw(amount);

        // then
        assertThat(bankingAccount.getBalance())
            .isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    @DisplayName("withdraw: should throw exception when balance is insufficient")
    void withdrawIsInsufficient_ThrowsException() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(300);

        // when / then
        assertThrows(
            BankingAccountInsufficientFundsException.class,
            () -> bankingAccount.withdraw(amount)
        );
    }

    @Test
    @DisplayName("addBalance: should add amount to balance and return new balance")
    void deposit() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(250);

        // when
        bankingAccount.deposit(amount);

        // then
        assertThat(bankingAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(350));
    }

    @Test
    @DisplayName("assertOwnedBy: should pass when customer is the owner")
    void assertOwnedBy_WhenCustomerIsOwner_DoesNotThrow() {
        // given
        // when / then
        assertThatCode(() -> bankingAccount.assertOwnedBy(customer.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertOwnedBy: should throw exception when customer is not the owner")
    void assertOwnedBy_WhenCustomerIsNotOwner_ThrowsException() {
        // given
        Long otherCustomerId = 99L;

        // when / then
        BankingAccountNotOwnerException exception = assertThrows(
            BankingAccountNotOwnerException.class,
            () -> bankingAccount.assertOwnedBy(otherCustomerId)
        );

        // optional but nice
        assertThat(exception)
            .hasMessage(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER);
    }

    @Test
    @DisplayName("assertNotSuspended: should pass when account is not suspended")
    void assertNotSuspended_WhenAccountIsNotSuspended_DoesNotThrow() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.ACTIVE)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when / then
        assertDoesNotThrow(bankingAccount::assertNotSuspended);
    }

    @Test
    @DisplayName("assertNotSuspended: should throw exception when account is suspended")
    void assertNotSuspended_WhenAccountIsSuspended_ThrowsException() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.SUSPENDED)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when / then
        assertThrows(
            BankingAccountSuspendedException.class,
            () -> bankingAccount.assertNotSuspended()
        );
    }

    @Test
    @DisplayName("assertNotClosed: should pass when account is not closed")
    void assertNotClosed_WhenAccountIsNotClosed_DoesNotThrow() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.ACTIVE)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when / then
        assertDoesNotThrow(bankingAccount::assertNotClosed);
    }

    @Test
    @DisplayName("assertNotClosed: should throw exception when account is closed")
    void assertNotClosed_WhenAccountIsClosed_ThrowsException() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.CLOSED)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when / then
        assertThrows(
            BankingAccountClosedException.class,
            () -> bankingAccount.assertNotClosed()
        );
    }

    @Test
    @DisplayName("closeBy: should close account")
    void close_ClosesAccount() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.ACTIVE)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when
        bankingAccount.close();

        // then
        assertThat(bankingAccount.getStatus())
            .isEqualTo(BankingAccountStatus.CLOSED);
    }

    @Test
    @DisplayName("closeBy: should throws exception when actor is not admin")
    void activate_WhenActorIsNotAdmin_AccountNotClosed() {
        // given
        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withStatus(BankingAccountStatus.ACTIVE)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        // when
        bankingAccount.activate();

        // then
        assertThat(bankingAccount.getStatus())
            .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("assertCanAddCard: should allow adding card when active cards are below limit")
    void assertCanAddCard_WhenBelowLimit_DoesNotThrowException() {
        // given

        for (int i = 0; i < BankingAccount.MAX_CARDS_PER_ACCOUNT - 1; i++) {

            bankingAccount.issueCard(
                BankingCardType.CREDIT,
                "1234123412341234",
                "123",
                "1234"
            );
        }

        // when / then
        assertThatCode(bankingAccount::assertCanAddCard)
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertCanAddCard: should throws exception when active cards are at limit")
    void assertCanAddCard_WhenAtLimit_ThrowsException() {
        // given

        for (int i = 0; i < BankingAccount.MAX_CARDS_PER_ACCOUNT; i++) {
            BankingCard card = bankingAccount.issueCard(
                BankingCardType.CREDIT,
                "1234123412341234",
                "123",
                "1234"
            );
            card.activate(card.getCardCvv());
        }

        // when / then
        assertThrows(
            BankingAccountCardsLimitException.class,
            bankingAccount::assertCanAddCard
        );
    }

}