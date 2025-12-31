package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BankingAccountDepositTest extends AbstractServiceTest {

    @InjectMocks
    private BankingAccountDeposit bankingAccountDeposit;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @Mock
    private NotificationPublisher notificationPublisher;

    private Customer customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

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
    @DisplayName("Should returns a transaction when valid request")
    void execute_WhenValidRequest_ReturnsTransaction() {
        // given
        setUpContext(customer);

        BigDecimal initialBalance = bankingAccount.getBalance();
        BigDecimal depositAmount = BigDecimal.valueOf(3000);

        BankingTransaction transaction = new BankingTransaction(bankingAccount);
        transaction.setType(BankingTransactionType.DEPOSIT);
        transaction.setAmount(depositAmount);

        BankingAccountDepositRequest depositRequest = new BankingAccountDepositRequest(
                bankingAccount.getAccountNumber(),
                depositAmount
        );

        when(bankingAccountRepository.findById(bankingAccount.getId())).thenReturn(Optional.of(
                bankingAccount));

        when(bankingTransactionPersistenceService.record(
                any(BankingTransaction.class)
        )).thenAnswer(i -> i.getArgument(0));

        doNothing().when(notificationPublisher).publish(any(NotificationEvent.class));

        // then
        BankingTransaction result = bankingAccountDeposit.execute(
                bankingAccount.getId(),
                depositRequest
        );

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        BankingTransaction::getType,
                        BankingTransaction::getStatus,
                        BankingTransaction::getAmount,
                        BankingTransaction::getBalanceBefore,
                        BankingTransaction::getBalanceAfter

                ).containsExactly(
                        BankingTransactionType.DEPOSIT,
                        BankingTransactionStatus.COMPLETED,
                        depositAmount,
                        initialBalance,
                        initialBalance.add(depositAmount)
                );
    }
}