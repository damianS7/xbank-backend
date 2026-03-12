package com.damian.xBank.modules.banking.card.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardDisabledException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidPinException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardLockedException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotOwnerException;
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

public class BankingCardTest extends AbstractServiceTest {
    private User user;
    private User admin;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        admin = UserTestBuilder.aCustomer()
            .withId(2L)
            .withRole(UserRole.ADMIN)
            .withEmail("admin@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        user = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(user)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        user.addBankingAccount(bankingAccount);

        bankingCard = BankingCard
            .create(bankingAccount)
            .setCardNumber("1234123412341234");
    }


    @Test
    @DisplayName("should pass when user owns the card")
    void assertOwnedBy_WhenUserIsOwner_DoesNotThrow() {
        // given
        // when / then
        assertThatCode(() -> bankingCard.assertOwnedBy(user.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw exception when user is not the owner of the card")
    void assertOwnedBy_WhenUserIsNotOwner_ThrowsException() {
        // given
        Long otherUserId = 99L;

        // when / then
        BankingCardNotOwnerException exception = assertThrows(
            BankingCardNotOwnerException.class,
            () -> bankingCard.assertOwnedBy(otherUserId)
        );

        // optional but nice
        assertThat(exception)
            .hasMessage(ErrorCodes.BANKING_CARD_NOT_OWNER);
    }

    @Test
    @DisplayName("should pass when balance is greater than or equal to amount")
    void assertSufficientFunds_WhenBalanceIsEnough_DoesNotThrow() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(500));

        BigDecimal amount = BigDecimal.valueOf(200);

        // when / then
        assertThatCode(() -> bankingCard.assertSufficientFunds(amount))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should pass when amount is equal to balance")
    void assertSufficientFunds_WhenAmountIsEqualToBalance_DoesNotThrow() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(500));

        BigDecimal amount = bankingCard.getBalance();

        // when / then
        assertThatCode(() -> bankingCard.assertSufficientFunds(amount))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw exception when balance is insufficient")
    void assertSufficientFunds_WhenBalanceIsInsufficient_ThrowsException() {
        // given
        BankingAccount bankingAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(user)
            .withBalance(BigDecimal.valueOf(0))
            .withAccountNumber("US1200001111112233335555")
            .build();

        BankingCard bankingCard = BankingCard
            .create(bankingAccount)
            .setId(11L)
            .setStatus(BankingCardStatus.ACTIVE)
            .setCardNumber("1234123412341234")
            .setExpiration(CardExpiration.defaultExpiration())
            .setCardCvv("123")
            .setCardPin("1234");

        BigDecimal amount = BigDecimal.valueOf(300);

        // when
        BankingCardInsufficientFundsException exception =
            assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingCard.assertSufficientFunds(amount)
            );

        // then
        assertThat(exception)
            .hasMessage(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("should pass when given pin matches the pin card")
    void assertCorrectPin_WhenPinMatches_DoesNotThrow() {
        // given
        bankingCard.setCardPin("1234");
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(
            () -> bankingCard.assertCorrectPin(bankingCard.getCardPin()));
    }

    @Test
    @DisplayName("should throw exception when given pin not matches with the pin card")
    void assertCorrectPin_WhenPinNotMatches_ThrowsException() {
        // given
        bankingCard.setCardPin("1234");
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // when / then
        assertThrows(
            BankingCardInvalidPinException.class,
            () -> bankingCard.assertCorrectPin("0000")
        );
    }


    @Test
    @DisplayName("should pass when card is not disabled")
    void assertEnabled_WhenCardIsActive_DoesNotThrow() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(bankingCard::assertEnabled);
    }

    @Test
    @DisplayName("should throw exception when card is disabled")
    void assertEnabled_WhenCardIsDisabled_ThrowsException() {
        // given
        bankingCard.setStatus(BankingCardStatus.DISABLED);

        // when / then
        assertThrows(
            BankingCardDisabledException.class,
            () -> bankingCard.assertEnabled()
        );
    }

    @Test
    @DisplayName("should pass when card is not locked")
    void assertUnlocked_WhenCardIsActive_DoesNotThrow() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(bankingCard::assertUnlocked);
    }

    @Test
    @DisplayName("should throw exception when card is locked")
    void assertUnlocked_WhenCardIsLocked_ThrowsException() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);
        bankingCard.setStatus(BankingCardStatus.LOCKED);

        // when / then
        assertThrows(
            BankingCardLockedException.class,
            () -> bankingCard.assertUnlocked()
        );
    }

    @Test
    @DisplayName("should pass when card is can spend")
    void assertCanSpend_WhenValid_DoesNotThrow() {
        // given
        bankingAccount.deposit(BigDecimal.valueOf(100));
        bankingCard.setCardPin("1234");
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // when / then
        assertDoesNotThrow(
            () -> bankingCard.assertCanSpend(
                user, BigDecimal.valueOf(100),
                bankingCard.getCardPin()
            )
        );
    }
}
