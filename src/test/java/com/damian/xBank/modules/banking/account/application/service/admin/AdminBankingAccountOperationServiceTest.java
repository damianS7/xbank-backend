package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
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
import static org.mockito.Mockito.when;

public class AdminBankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private AdminBankingAccountOperationService adminBankingAccountOperationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionAccountService bankingTransactionAccountService;

    @Test
    @DisplayName("Should deposit")
    void shouldDeposit() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        //        setUpContext(customer);

        BigDecimal givenDepositAmount = BigDecimal.valueOf(3000);

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(2L);
        givenBankingAccount.setBalance(BigDecimal.ZERO);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        BankingTransaction givenBankingTransaction = new BankingTransaction(givenBankingAccount);
        givenBankingTransaction.setTransactionType(BankingTransactionType.DEPOSIT);
        givenBankingTransaction.setAmount(givenDepositAmount);

        BankingAccountDepositRequest depositRequest = new BankingAccountDepositRequest(
                givenBankingAccount.getAccountNumber(),
                givenDepositAmount
        );

        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(
                givenBankingAccount));

        when(bankingTransactionAccountService.generateTransaction(
                any(BankingAccount.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransaction);

        when(bankingTransactionAccountService.persistTransaction(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        BankingTransaction transaction = adminBankingAccountOperationService.deposit(
                givenBankingAccount.getId(),
                depositRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getTransactionType()).isEqualTo(givenBankingTransaction.getTransactionType());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(givenBankingAccount.getBalance()).isEqualTo(givenDepositAmount);
    }

}