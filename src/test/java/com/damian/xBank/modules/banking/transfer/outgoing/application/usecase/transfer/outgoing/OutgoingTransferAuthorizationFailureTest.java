package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.OutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.BankingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OutgoingTransferAuthorizationFailureTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

    @InjectMocks
    private OutgoingTransferAuthorizationFailure outgoingTransferAuthorizationFailure;

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
            .withBalance(BigDecimal.valueOf(3000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    void transferFailure_ChangesTransferStatusToFailedAndRevertsBalance() {
        // given
        BigDecimal accountInitialBalance = bankingAccount.getBalance();
        OutgoingTransferFailureRequest request = new OutgoingTransferFailureRequest(
            "123/123",
            "Error"
        );

        OutgoingTransfer transfer = BankingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(bankingAccount)
            .withToAccount(null)
            .withToAccountIban("ES9900001111112233334444")
            .withAmount(accountInitialBalance)
            .withDescription("a gift!")
            .build();

        transfer.confirm();
        System.out.println(transfer.getAmount());
        System.out.println(transfer.getFromAccount().getBalance());

        // when
        when(outgoingTransferRepository.findByProviderAuthorizationId(anyString()))
            .thenReturn(Optional.of(transfer));

        // then
        outgoingTransferAuthorizationFailure.execute(request);
        System.out.println(transfer.getAmount());
        System.out.println(transfer.getFromAccount().getBalance());

        assertThat(transfer)
            .extracting(
                OutgoingTransfer::getStatus
            )
            .isEqualTo(
                OutgoingTransferStatus.FAILED
            );

        assertThat(transfer.getFromAccount())
            .extracting(BankingAccount::getBalance)
            .isEqualTo(accountInitialBalance);
    }
}
