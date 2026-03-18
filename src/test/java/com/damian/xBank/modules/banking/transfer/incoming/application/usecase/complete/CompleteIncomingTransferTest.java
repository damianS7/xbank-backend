package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.repository.IncomingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
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

public class CompleteIncomingTransferTest extends AbstractServiceTest {

    @Mock
    private IncomingTransferRepository incomingTransferRepository;

    @InjectMocks
    private CompleteIncomingTransfer completeIncomingTransfer;

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
            .withBalance(BigDecimal.valueOf(0))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    void completeIncomingTransfer_WhenValidRequest_ProcessTransferAndAddBalance() {
        // given
        CompleteIncomingTransferCommand command = new CompleteIncomingTransferCommand(
            "123456789"
        );

        IncomingTransfer transfer = IncomingTransfer.create(
            "ES001122334455667788",
            bankingAccount,
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "DAVID"
        );

        transfer.authorize("1234-1234");

        ArgumentCaptor<IncomingTransfer> captor = ArgumentCaptor.forClass(IncomingTransfer.class);

        // when
        when(incomingTransferRepository.findByProviderAuthorizationId(anyString()))
            .thenReturn(Optional.of(transfer));
        when(incomingTransferRepository.save(any(IncomingTransfer.class)))
            .thenAnswer(i -> i.getArgument(0));

        // then
        completeIncomingTransfer.execute(command);

        verify(incomingTransferRepository).save(captor.capture());

        IncomingTransfer savedTransfer = captor.getValue();
        assertThat(savedTransfer.getTransaction())
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
