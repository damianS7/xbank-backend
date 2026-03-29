package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATM;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATMCommand;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATMResult;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class WithdrawFromATMTest extends AbstractServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private WithdrawFromATM withdrawFromATM;

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
    @DisplayName("should return transaction resulted from withdraw")
    void withdraw_WhenValidRequest_ReturnsTransaction() {
        // given
        setUpContext(customer);

        WithdrawFromATMCommand command = new WithdrawFromATMCommand(
            bankingCard.getId(),
            bankingAccount.getBalance(),
            bankingCard.getCardPin()
        );

        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withCard(bankingCard)
            .withType(BankingTransactionType.WITHDRAWAL)
            .withAmount(command.amount())
            .build();

        when(bankingCardRepository.findById(anyLong())).
            thenReturn(Optional.of(bankingCard));

        when(bankingTransactionRepository.save(
            any(BankingTransaction.class)
        )).thenReturn(transaction);

        // then
        WithdrawFromATMResult result = withdrawFromATM.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(transaction.getType());
        assertThat(result.status()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("should throw exception when insufficient funds")
    void withdraw_WhenInsufficientFunds_ThrowsException() {
        // given
        setUpContext(customer);

        BankingAccount bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(1L)
            .build();

        BankingCard bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .withId(11L)
            .build();

        WithdrawFromATMCommand command = new WithdrawFromATMCommand(
            bankingCard.getId(),
            bankingAccount.getBalance().add(BigDecimal.ONE),
            bankingCard.getCardPin()
        );

        when(bankingCardRepository.findById(anyLong()))
            .thenReturn(Optional.of(bankingCard));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
            BankingCardInsufficientFundsException.class,
            () -> withdrawFromATM.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);

        // then
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}