package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.domain.exception.*;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BankingCardSpendTest extends AbstractServiceTest {
    @Mock
    private NotificationEventFactory notificationEventFactory;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @InjectMocks
    private BankingCardSpend bankingCardSpend;

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
                .setAccountNumber("US9900001111112233334444");


        bankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }


    @Test
    @DisplayName("should return transaction resulted from spend")
    void spend_WhenValidRequest_ReturnsTransaction() {
        // given
        setUpContext(customer);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                bankingAccount.getBalance(),
                bankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(spendRequest.amount());
        givenBankingTransaction.setDescription(spendRequest.description());

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        when(bankingTransactionPersistenceService.record(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        BankingTransaction transaction = bankingCardSpend.execute(
                bankingCard.getId(),
                spendRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(givenBankingTransaction.getType());
        assertThat(transaction.getDescription()).isEqualTo(givenBankingTransaction.getDescription());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void spend_WhenCardNotFound_ThrowsException() {
        // given
        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                BigDecimal.valueOf(100),
                "1234",
                "Amazon.com"
        );

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        BankingCardNotFoundException exception = assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardSpend.execute(
                        1L,
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when customer is not the card owner")
    void spend_WhenNotOwnerCard_ThrowsException() {
        // given
        User customerNotOwner = UserTestBuilder.aCustomer()
                                               .withId(2L)
                                               .withEmail("customerNotOwner@demo.com")
                                               .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                               .build();

        setUpContext(customerNotOwner);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                bankingAccount.getBalance(),
                bankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(spendRequest.amount());
        givenBankingTransaction.setDescription(spendRequest.description());

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardNotOwnerException exception = assertThrows(
                BankingCardNotOwnerException.class,
                () -> bankingCardSpend.execute(
                        bankingCard.getId(),
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_OWNER);
    }

    @Test
    @DisplayName("should throw exception when card is not active")
    void spend_WhenCardNotActive_ThrowsException() {
        // given

        setUpContext(customer);

        bankingCard.setStatus(BankingCardStatus.DISABLED);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                bankingAccount.getBalance(),
                bankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardDisabledException exception = assertThrows(
                BankingCardDisabledException.class,
                () -> bankingCardSpend.execute(
                        bankingCard.getId(),
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_DISABLED);
    }

    @Test
    @DisplayName("should throw exception when card is locked")
    void spend_WhenCardLocked_ThrowsException() {
        // given
        setUpContext(customer);
        bankingCard.setStatus(BankingCardStatus.ACTIVE);
        bankingCard.setStatus(BankingCardStatus.LOCKED);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                bankingAccount.getBalance(),
                bankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardLockedException exception = assertThrows(
                BankingCardLockedException.class,
                () -> bankingCardSpend.execute(
                        bankingCard.getId(),
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_LOCKED);
    }

    @Test
    @DisplayName("should throw exception when insufficient funds")
    void spend_WhenInsufficientFunds_ThrowsException() {
        // given
        setUpContext(customer);

        bankingAccount.setBalance(BigDecimal.valueOf(0));

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                BigDecimal.valueOf(1000),
                bankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingCardSpend.execute(
                        bankingCard.getId(),
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    }
}