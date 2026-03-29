package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPaymentCommand;
import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPaymentResult;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotActiveException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.CardNumber;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizeCardPaymentTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private AuthorizeCardPayment cardAuthorize;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .withId(11L)
            .build();
    }

    @Test
    @DisplayName("should authorize payment and return response")
    void authorizePayment_WhenValidRequest_ReturnsAuthorized() {
        // given
        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            "Amazon.com",
            "",
            bankingCard.getCardNumber(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        when(bankingCardRepository.findByCardNumber(any(CardNumber.class)))
            .thenReturn(Optional.of(bankingCard));
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // then
        AuthorizeCardPaymentResult response = cardAuthorize.execute(command);
        ArgumentCaptor<BankingTransaction> argCaptor = ArgumentCaptor.forClass(BankingTransaction.class);
        verify(bankingTransactionRepository).save(argCaptor.capture());
        BankingTransaction transaction = argCaptor.getValue();

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeCardPaymentResult::status,
                AuthorizeCardPaymentResult::authorizationId,
                AuthorizeCardPaymentResult::declineReason
            ).containsExactly(
                PaymentAuthorizationStatus.AUTHORIZED,
                transaction.getAuthorizationId(),
                null
            );

        assertThat(transaction)
            .isNotNull()
            .extracting(BankingTransaction::getPaymentStatus)
            .isEqualTo(BankingTransactionPaymentStatus.AUTHORIZED);

        assertThat(transaction.getAuthorizationId())
            .hasSizeGreaterThan(10);
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void authorizePayment_WhenCardNotFound_ThrowsException() {
        // given
        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            "Amazon.com",
            "",
            bankingCard.getCardNumber(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingCardRepository.findByCardNumber(any(CardNumber.class)))
            .thenReturn(Optional.empty());

        // then
        BankingCardNotFoundException exception = assertThrows(
            BankingCardNotFoundException.class,
            () -> cardAuthorize.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when card is not active")
    void authorizePayment_WhenCardNotActive_ThrowsException() {
        // given
        BankingCard bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .disabled()
            .build();

        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            "Amazon.com",
            "",
            bankingCard.getCardNumber(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingCardRepository.findByCardNumber(any(CardNumber.class)))
            .thenReturn(Optional.of(bankingCard));

        // then
        BankingCardNotActiveException exception = assertThrows(
            BankingCardNotActiveException.class,
            () -> cardAuthorize.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_ACTIVE);
    }

    @Test
    @DisplayName("should throw exception when card is locked")
    void authorizePayment_WhenCardLocked_ThrowsException() {
        // given
        BankingCard bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .locked()
            .build();

        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            "Amazon.com",
            "",
            bankingCard.getCardNumber(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingCardRepository.findByCardNumber(any(CardNumber.class)))
            .thenReturn(Optional.of(bankingCard));

        // then
        BankingCardNotActiveException exception = assertThrows(
            BankingCardNotActiveException.class,
            () -> cardAuthorize.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_ACTIVE);
    }

    @Test
    @DisplayName("should throw exception when insufficient funds")
    void authorizePayment_WhenInsufficientFunds_ThrowsException() {
        // given
        BankingAccount bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(1L)
            .build();

        BankingCard bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .withId(11L)
            .build();

        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            "Amazon.com",
            "",
            bankingCard.getCardNumber(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingCardRepository.findByCardNumber(any(CardNumber.class)))
            .thenReturn(Optional.of(bankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
            BankingCardInsufficientFundsException.class,
            () -> cardAuthorize.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    }
}