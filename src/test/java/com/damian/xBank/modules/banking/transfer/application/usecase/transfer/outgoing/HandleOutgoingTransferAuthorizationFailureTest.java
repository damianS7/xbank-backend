package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.HandleOutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class HandleOutgoingTransferAuthorizationFailureTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

    @InjectMocks
    private HandleOutgoingTransferAuthorizationFailure handleOutgoingTransferAuthorizationFailure;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccount
            .create(customer)
            .setId(5L)
            .setCurrency(BankingAccountCurrency.EUR)
            .setType(BankingAccountType.SAVINGS)
            .setAccountNumber("US9900001111112233334444");
    }

    @Test
    void transferFailure_ChangesTransferStatusToFailedAndRevertsBalance() {
        // given
        bankingAccount.setBalance(BigDecimal.valueOf(100));

        OutgoingTransferFailureRequest request = new OutgoingTransferFailureRequest(
            "123/123",
            "Error"
        );

        BankingTransfer transfer = BankingTransfer.create(
            bankingAccount,
            null,
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingTransferRepository.findByProviderAuthorizationId(anyString()))
            .thenReturn(Optional.of(transfer));

        // then
        handleOutgoingTransferAuthorizationFailure.execute(request);

        assertThat(transfer)
            .extracting(
                BankingTransfer::getStatus
            )
            .isEqualTo(
                BankingTransferStatus.REJECTED
            );

        assertThat(transfer.getFromAccount())
            .extracting(BankingAccount::getBalance)
            .isEqualTo(transfer.getAmount());
    }
}
