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
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BankingCardSpendTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @InjectMocks
    private BankingCardSpend bankingCardSpend;

    private Customer customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

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
    @DisplayName("Should spend")
    void shouldSpend() {
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
                any(BankingCard.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransaction);

        doNothing().when(notificationPublisher).publish(any(NotificationEvent.class));

        // then
        BankingTransaction transaction = bankingCardSpend.execute(
                bankingCard.getId(),
                spendRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(givenBankingTransaction.getType());
        assertThat(transaction.getDescription()).isEqualTo(givenBankingTransaction.getDescription());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.PENDING);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should fail to spend when card not found")
    void shouldFailToSpendWhenCardNotFound() {
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
    @DisplayName("Should fail to spend when card does not belong to customer")
    void shouldFailToSpendWhenCardNotBelongToCustomer() {
        // given
        Customer customerNotOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerNotOwner@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

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
    @DisplayName("Should fail to spend when card is not active")
    void shouldFailToSpendWhenCardIsNotActive() {
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
    @DisplayName("Should fail to spend when card is locked")
    void shouldFailToSpendWhenCardIsLocked() {
        // given

        setUpContext(customer);
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
    @DisplayName("Should fail to spend when card insufficient funds")
    void shouldFailToSpendWhenInsufficientFunds() {
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