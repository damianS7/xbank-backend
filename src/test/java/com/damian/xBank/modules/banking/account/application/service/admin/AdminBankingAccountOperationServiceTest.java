package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
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

public class AdminBankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private AdminBankingAccountOperationService adminBankingAccountOperationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionService bankingTransactionService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Test
    @DisplayName("Should deposit")
    void shouldDeposit() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        BigDecimal depositAmount = BigDecimal.valueOf(3000);

        BankingAccount customerBankingAccount = new BankingAccount(customer);
        customerBankingAccount.setId(2L);
        customerBankingAccount.setBalance(BigDecimal.ZERO);
        customerBankingAccount.setAccountNumber("US9900001111112233334444");

        BankingTransaction transaction = new BankingTransaction(customerBankingAccount);
        transaction.setType(BankingTransactionType.DEPOSIT);
        transaction.setAmount(depositAmount);

        BankingAccountDepositRequest depositRequest = new BankingAccountDepositRequest(
                customerBankingAccount.getAccountNumber(),
                depositAmount
        );

        when(bankingAccountRepository.findById(customerBankingAccount.getId())).thenReturn(Optional.of(
                customerBankingAccount));

        when(bankingTransactionService.record(
                any(BankingTransaction.class)
        )).thenReturn(transaction);

        doNothing().when(notificationPublisher).publish(any(NotificationEvent.class));

        // then
        transaction = adminBankingAccountOperationService.deposit(
                customerBankingAccount.getId(),
                depositRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(BankingTransactionType.DEPOSIT);
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(customerBankingAccount.getBalance()).isEqualTo(depositAmount);
    }
}