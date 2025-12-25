package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.*;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BankingCardOperationServiceTest extends AbstractServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingTransactionService bankingTransactionService;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardOperationService bankingCardOperationService;

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create()
                .setOwner(customer)
                .setId(5L)
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setId(11L)
                .setBankingAccount(customerBankingAccount)
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
                customerBankingAccount.getBalance(),
                customerBankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(spendRequest.amount());
        givenBankingTransaction.setDescription(spendRequest.description());

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingTransactionService.record(
                any(BankingCard.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransaction);

        doNothing().when(notificationService).publish(any(NotificationEvent.class));

        // then
        BankingTransaction transaction = bankingCardOperationService.spend(
                customerBankingCard.getId(),
                spendRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(givenBankingTransaction.getType());
        assertThat(transaction.getDescription()).isEqualTo(givenBankingTransaction.getDescription());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.PENDING);
        assertThat(customerBankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
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
                () -> bankingCardOperationService.spend(
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
                customerBankingAccount.getBalance(),
                customerBankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(spendRequest.amount());
        givenBankingTransaction.setDescription(spendRequest.description());

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        // then
        BankingCardNotOwnerException exception = assertThrows(
                BankingCardNotOwnerException.class,
                () -> bankingCardOperationService.spend(
                        customerBankingCard.getId(),
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

        customerBankingCard.setCardStatus(BankingCardStatus.DISABLED);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                customerBankingAccount.getBalance(),
                customerBankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        // then
        BankingCardDisabledException exception = assertThrows(
                BankingCardDisabledException.class,
                () -> bankingCardOperationService.spend(
                        customerBankingCard.getId(),
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
        customerBankingCard.setCardStatus(BankingCardStatus.LOCKED);

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                customerBankingAccount.getBalance(),
                customerBankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        // then
        BankingCardLockedException exception = assertThrows(
                BankingCardLockedException.class,
                () -> bankingCardOperationService.spend(
                        customerBankingCard.getId(),
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

        customerBankingAccount.setBalance(BigDecimal.valueOf(0));

        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
                BigDecimal.valueOf(1000),
                customerBankingCard.getCardPin(),
                "Amazon.com"
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
        givenBankingTransaction.setDescription("Amazon.com");


        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingCardOperationService.spend(
                        customerBankingCard.getId(),
                        spendRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("Should withdraw")
    void shouldWithdraw() {
        // given
        setUpContext(customer);

        BankingCardWithdrawRequest spendRequest = new BankingCardWithdrawRequest(
                customerBankingAccount.getBalance(),
                customerBankingCard.getCardPin()
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.WITHDRAWAL);
        givenBankingTransaction.setAmount(spendRequest.amount());
        givenBankingTransaction.setDescription("WITHDRAWAL");

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingTransactionService.record(
                any(BankingCard.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransaction);

        // then
        BankingTransaction transaction = bankingCardOperationService.withdraw(
                customerBankingCard.getId(),
                spendRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(givenBankingTransaction.getType());
        assertThat(transaction.getDescription()).isEqualTo(givenBankingTransaction.getDescription());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.PENDING);
        assertThat(customerBankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should fail to withdraw when insufficient funds")
    void shouldFailToWithdrawWhenInsufficientFunds() {
        // given
        setUpContext(customer);

        BankingCardWithdrawRequest withdrawRequest = new BankingCardWithdrawRequest(
                customerBankingAccount.getBalance().add(BigDecimal.ONE),
                customerBankingCard.getCardPin()
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(customerBankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.WITHDRAWAL);
        givenBankingTransaction.setAmount(withdrawRequest.amount());
        givenBankingTransaction.setDescription("WITHDRAWAL");

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingCardOperationService.withdraw(
                        customerBankingCard.getId(),
                        withdrawRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);

        // then
        assertThat(customerBankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

}
