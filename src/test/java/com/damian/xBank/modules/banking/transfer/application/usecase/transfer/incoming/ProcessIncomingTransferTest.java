package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.application.usecase.incoming.ProcessIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.IncomingTransferRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.BankingAccountTestBuilder;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessIncomingTransferTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private ProcessIncomingTransfer processIncomingTransfer;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(0))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    void processIncomingTransfer_WhenValidRequest_ProcessTransferAndAddBalance() {
        // given
        IncomingTransferRequest request = new IncomingTransferRequest(
            "123456789",
            bankingAccount.getAccountNumber(),
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "JOHN DOE"
        );

        bankingAccount.deposit(BigDecimal.valueOf(0));

        ArgumentCaptor<BankingTransaction> captor =
            ArgumentCaptor.forClass(BankingTransaction.class);

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
            .thenAnswer(i -> i.getArgument(0));

        // then
        processIncomingTransfer.execute(request);

        verify(bankingTransactionRepository).save(captor.capture());

        BankingTransaction savedTransaction = captor.getValue();
        assertThat(savedTransaction)
            .isNotNull()
            .extracting(
                BankingTransaction::getAmount,
                BankingTransaction::getBalanceBefore,
                BankingTransaction::getBalanceAfter
            ).containsExactly(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(100)
            );

        assertThat(bankingAccount)
            .extracting(
                BankingAccount::getBalance
            ).isEqualTo(
                BigDecimal.valueOf(100)
            );
    }
}
