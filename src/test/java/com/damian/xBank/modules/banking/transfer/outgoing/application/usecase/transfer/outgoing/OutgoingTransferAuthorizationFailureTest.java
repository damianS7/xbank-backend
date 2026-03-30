package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.OutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.OutgoingTransferTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
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
    private BankingAccount fromAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        fromAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(3000))
            .build();
    }

    @Test
    void transferFailure_ChangesTransferStatusToFailedAndRevertsBalance() {
        // given
        BigDecimal accountInitialBalance = fromAccount.getBalance();
        OutgoingTransferFailureRequest request = new OutgoingTransferFailureRequest(
            "123/123",
            "Error"
        );

        OutgoingTransfer transfer = OutgoingTransferTestFactory.aExternalTransfer(
                fromAccount,
                "ES9900001111112233334444"
            )
            .withId(1L)
            .withAmount(accountInitialBalance)
            .withDescription("a gift!")
            .build();
        transfer.confirm();

        // when
        when(outgoingTransferRepository.findByProviderAuthorizationId(anyString()))
            .thenReturn(Optional.of(transfer));

        // then
        outgoingTransferAuthorizationFailure.execute(request);

        assertThat(transfer)
            .extracting(
                OutgoingTransfer::getStatus
            )
            .isEqualTo(OutgoingTransferStatus.FAILED);

        assertThat(transfer.getFromAccount())
            .extracting(BankingAccount::getBalance)
            .isEqualTo(accountInitialBalance);
    }
}
