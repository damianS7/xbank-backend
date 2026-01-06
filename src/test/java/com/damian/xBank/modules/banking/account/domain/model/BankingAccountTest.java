package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
        admin = UserTestBuilder.aCustomer()
                               .withId(2L)
                               .withEmail("admin@demo.com")
                               .withRole(UserRole.ADMIN)
                               .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                               .build();

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@demo.com")
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .build();

        bankingAccount = BankingAccount
                .create(customer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("assertSufficientFunds: should pass when balance is greater than or equal to amount")
    void assertSufficientFunds_WhenBalanceIsEnough_DoesNotThrow() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(500));

        BigDecimal amount = BigDecimal.valueOf(200);

        // when / then
        assertThatCode(() -> bankingAccount.assertSufficientFunds(amount))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertSufficientFunds: should pass when amount is equal to balance")
    void assertSufficientFunds_WhenAmountIsEqualToBalance_DoesNotThrow() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(500));

        BigDecimal amount = bankingAccount.getBalance();

        // when / then
        assertThatCode(() -> bankingAccount.assertSufficientFunds(amount))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertSufficientFunds: should throw exception when balance is insufficient")
    void assertSufficientFunds_WhenBalanceIsInsufficient_ThrowsException() {
        // given
        BankingAccount account = new BankingAccount();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(300);

        // when
        BankingAccountInsufficientFundsException exception =
                assertThrows(
                        BankingAccountInsufficientFundsException.class,
                        () -> account.assertSufficientFunds(amount)
                );

        // then
        assertThat(exception)
                .hasMessage(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("subtractBalance: should subtract amount when balance is sufficient")
    void subtractBalance_WhenBalanceIsSufficient_SubtractsAmount() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(500));

        BigDecimal amount = BigDecimal.valueOf(200);

        // when
        bankingAccount.subtractBalance(amount);

        // then
        assertThat(bankingAccount.getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    @DisplayName("subtractBalance: should throw exception when balance is insufficient")
    void subtractBalance_WhenBalanceIsInsufficient_ThrowsException() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(300);

        // when / then
        assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> bankingAccount.subtractBalance(amount)
        );
    }

    @Test
    @DisplayName("addBalance: should add amount to balance and return new balance")
    void addBalance_WhenCalled_AddsAmountAndReturnsNewBalance() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(250);

        // when
        BigDecimal result = bankingAccount.addBalance(amount);

        // then
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(350));
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
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(bankingAccount::assertNotSuspended);
    }

    @Test
    @DisplayName("assertNotSuspended: should throw exception when account is suspended")
    void assertNotSuspended_WhenAccountIsSuspended_ThrowsException() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

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
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(bankingAccount::assertNotClosed);
    }

    @Test
    @DisplayName("assertNotClosed: should throw exception when account is closed")
    void assertNotClosed_WhenAccountIsClosed_ThrowsException() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.CLOSED);

        // when / then
        assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccount.assertNotClosed()
        );
    }

    @Test
    @DisplayName("activateBy: should activate account when actor is admin")
    void activateBy_WhenActorIsAdmin_ActivatesAccount() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        // when
        bankingAccount.activateBy(admin);

        // then
        assertThat(bankingAccount.getStatus())
                .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("activateBy: should throws exception when actor is not admin")
    void activateBy_WhenActorIsNotAdmin_AccountNotActivated() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        // when
        bankingAccount.activateBy(customer);

        // then
        assertThat(bankingAccount.getStatus())
                .isEqualTo(BankingAccountStatus.SUSPENDED);
    }

    @Test
    @DisplayName("closeBy: should activate account when actor is admin")
    void closeBy_WhenActorIsAdmin_ClosesAccount() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);

        // when
        bankingAccount.closeBy(admin);

        // then
        assertThat(bankingAccount.getStatus())
                .isEqualTo(BankingAccountStatus.CLOSED);
    }

    @Test
    @DisplayName("closeBy: should throws exception when actor is not admin")
    void closeBy_WhenActorIsNotAdmin_AccountNotClosed() {
        // given
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);

        // when
        bankingAccount.activateBy(customer);

        // then
        assertThat(bankingAccount.getStatus())
                .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("assertCanAddCard: should allow adding card when active cards are below limit")
    void assertCanAddCard_WhenBelowLimit_DoesNotThrowException() {
        // given

        for (int i = 0; i < bankingAccount.getCardLimit() - 1; i++) {
            BankingCard card = BankingCard.create(bankingAccount);
            card.setStatus(BankingCardStatus.ACTIVE);
            bankingAccount.getBankingCards().add(card);
        }

        // when / then
        assertThatCode(bankingAccount::assertCanAddCard)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("assertCanAddCard: should throws exception when active cards are at limit")
    void assertCanAddCard_WhenAtLimit_ThrowsException() {
        // given

        for (int i = 0; i < bankingAccount.getCardLimit(); i++) {
            BankingCard card = BankingCard.create(bankingAccount);
            card.setStatus(BankingCardStatus.ACTIVE);
            bankingAccount.getBankingCards().add(card);
        }

        // when / then
        assertThrows(
                BankingAccountCardsLimitException.class,
                bankingAccount::assertCanAddCard
        );
    }

}