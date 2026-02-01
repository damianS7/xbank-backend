package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
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

public class BankingCardWithdrawTest extends AbstractServiceTest {

    @Mock
    private NotificationEventFactory notificationEventFactory;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardWithdraw bankingCardWithdraw;

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
                .setStatus(BankingCardStatus.ACTIVE)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("should return transaction resulted from withdraw")
    void withdraw_WhenValidRequest_ReturnsTransaction() {
        // given
        setUpContext(customer);

        BankingCardWithdrawRequest spendRequest = new BankingCardWithdrawRequest(
                bankingAccount.getBalance(),
                bankingCard.getCardPin()
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.WITHDRAWAL);
        givenBankingTransaction.setAmount(spendRequest.amount());

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        when(bankingTransactionPersistenceService.record(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        BankingTransaction transaction = bankingCardWithdraw.execute(
                bankingCard.getId(),
                spendRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(givenBankingTransaction.getType());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("should throw exception when insufficient funds")
    void withdraw_WhenInsufficientFunds_ThrowsException() {
        // given
        setUpContext(customer);

        BankingCardWithdrawRequest withdrawRequest = new BankingCardWithdrawRequest(
                bankingAccount.getBalance().add(BigDecimal.ONE),
                bankingCard.getCardPin()
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.WITHDRAWAL);
        givenBankingTransaction.setAmount(withdrawRequest.amount());
        givenBankingTransaction.setDescription("WITHDRAWAL");

        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingCardWithdraw.execute(
                        bankingCard.getId(),
                        withdrawRequest
                )
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);

        // then
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}