package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardDisabledException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardLockedException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthorizeCardPaymentTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @InjectMocks
    private AuthorizeCardPayment cardAuthorize;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@demo.com")
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .build();

        bankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        bankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setExpiredDate(LocalDate.now().plusYears(1))
                .setCardCvv("123")
                .setCardPin("1234");
    }


    @Test
    @DisplayName("should authorize payment and return response")
    void authorizePayment_WhenValidRequest_ReturnsAuthorized() {
        // given
        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                bankingCard.getExpiredDate().getMonthValue(),
                bankingCard.getExpiredDate().getYear(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setId(1L);
        givenBankingTransaction.setBankingCard(bankingCard);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(bankingCard));

        when(bankingTransactionPersistenceService.record(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        PaymentAuthorizationResponse response = cardAuthorize.execute(request);
        assertThat(response)
                .isNotNull()
                .extracting(
                        PaymentAuthorizationResponse::status,
                        PaymentAuthorizationResponse::authorizationId,
                        PaymentAuthorizationResponse::declineReason
                ).containsExactly(
                        PaymentAuthorizationStatus.AUTHORIZED,
                        givenBankingTransaction.getId().toString(),
                        null
                );
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void authorizePayment_WhenCardNotFound_ThrowsException() {
        // given
        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                bankingCard.getExpiredDate().getMonthValue(),
                bankingCard.getExpiredDate().getYear(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setId(1L);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        // then
        BankingCardNotFoundException exception = assertThrows(
                BankingCardNotFoundException.class,
                () -> cardAuthorize.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when card is not active")
    void authorizePayment_WhenCardNotActive_ThrowsException() {
        // given
        bankingCard.setStatus(BankingCardStatus.DISABLED);

        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                bankingCard.getExpiredDate().getMonthValue(),
                bankingCard.getExpiredDate().getYear(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setId(1L);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardDisabledException exception = assertThrows(
                BankingCardDisabledException.class,
                () -> cardAuthorize.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_DISABLED);
    }

    @Test
    @DisplayName("should throw exception when card is locked")
    void authorizePayment_WhenCardLocked_ThrowsException() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);
        bankingCard.setStatus(BankingCardStatus.LOCKED);

        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                bankingCard.getExpiredDate().getMonthValue(),
                bankingCard.getExpiredDate().getYear(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setId(1L);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardLockedException exception = assertThrows(
                BankingCardLockedException.class,
                () -> cardAuthorize.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_LOCKED);
    }

    @Test
    @DisplayName("should throw exception when insufficient funds")
    void authorizePayment_WhenInsufficientFunds_ThrowsException() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(0));
        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                bankingCard.getExpiredDate().getMonthValue(),
                bankingCard.getExpiredDate().getYear(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setId(1L);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> cardAuthorize.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    }
}