package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.deposit.DepositAccount;
import com.damian.xBank.modules.banking.account.application.usecase.deposit.DepositAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.deposit.DepositAccountResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountDepositNotAdminException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DepositAccountTest extends AbstractServiceTest {

    @InjectMocks
    private DepositAccount depositAccount;

    @Mock
    private NotificationEventFactory notificationEventFactory;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();
    }

    @Test
    @DisplayName("should return deposit transaction")
    void deposit_WhenValidRequest_ReturnsTransaction() {
        // given
        User customer = UserTestBuilder.builder()
            .withEmail("non-verified-user@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.VERIFIED)
            .withRole(UserRole.ADMIN)
            .build();

        setUpContext(customer);

        BigDecimal initialBalance = bankingAccount.getBalance();
        BigDecimal depositAmount = BigDecimal.valueOf(3000);
        BigDecimal afterBalance = initialBalance.add(depositAmount);

        DepositAccountCommand command = new DepositAccountCommand(
            bankingAccount.getId(),
            bankingAccount.getAccountNumber(),
            depositAmount
        );

        // when
        when(bankingAccountRepository.findById(bankingAccount.getId()))
            .thenReturn(Optional.of(bankingAccount));

        when(bankingTransactionRepository.save(
            any(BankingTransaction.class)
        )).thenAnswer(i -> i.getArgument(0));

        // then
        DepositAccountResult result = depositAccount.execute(command);

        // then
        assertThat(result)
            .isNotNull()
            .extracting(
                DepositAccountResult::type,
                DepositAccountResult::status,
                DepositAccountResult::balanceBefore,
                DepositAccountResult::amount,
                DepositAccountResult::balanceAfter
            ).containsExactly(
                BankingTransactionType.DEPOSIT,
                BankingTransactionStatus.COMPLETED,
                initialBalance,
                depositAmount,
                afterBalance
            );
    }

    @Test
    @DisplayName("should throw exception when not admin")
    void deposit_WhenNotAdmin_ThrowsException() {
        // given
        setUpContext(customer);

        BigDecimal depositAmount = BigDecimal.valueOf(3000);

        DepositAccountCommand command = new DepositAccountCommand(
            bankingAccount.getId(),
            bankingAccount.getAccountNumber(),
            depositAmount
        );

        // then
        BankingAccountDepositNotAdminException exception = assertThrows(
            BankingAccountDepositNotAdminException.class,
            () -> depositAccount.execute(command)
        );

        // then
        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_ACCOUNT_DEPOSIT_NOT_ADMIN);
    }
}