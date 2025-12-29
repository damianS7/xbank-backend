package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

public class BankingTransferRejectTest extends AbstractServiceTest {

    @InjectMocks
    private BankingTransferService bankingTransferService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private BankingTransactionService bankingTransactionService;

    private Customer fromCustomer;
    private Customer toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        fromAccount = BankingAccount
                .create(fromCustomer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        toAccount = BankingAccount
                .create(toCustomer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }


}