package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.repository.IncomingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
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

public class AuthorizeIncomingTransferTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private IncomingTransferRepository incomingTransferRepository;

    @InjectMocks
    private AuthorizeIncomingTransfer authorizeIncomingTransfer;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withAccountNumber("US9900001111112233334444")
            .withCurrency(BankingAccountCurrency.EUR)
            .build();
    }

    @Test
    void authorizeIncomingTransfer_AuthorizesTransfer() {
        // given
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            "1234",
            "ES001188222832838",
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        AuthorizeIncomingTransferResult response = authorizeIncomingTransfer.execute(command);

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status
            ).isEqualTo(
                TransferAuthorizationStatus.AUTHORIZED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountNotFound_RejectsTransfer() {
        // given
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            "1234",
            "ES001188222832838",
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.empty());

        // then
        AuthorizeIncomingTransferResult response = authorizeIncomingTransfer.execute(command);

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                TransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_NOT_FOUND
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountSuspended_RejectsTransfer() {
        // given
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            "1234",
            "ES001188222832838",
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        bankingAccount.suspend();

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        AuthorizeIncomingTransferResult response = authorizeIncomingTransfer.execute(command);

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                TransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_SUSPENDED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountClosed_RejectsTransfer() {
        // given
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            "1234",
            "ES001188222832838",
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        bankingAccount.close();

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        AuthorizeIncomingTransferResult response = authorizeIncomingTransfer.execute(command);

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                TransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_CLOSED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenCurrencyMismatch_RejectsTransfer() {
        // given
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            "1234",
            "ES001188222832838",
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "USD",
            "from David"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        AuthorizeIncomingTransferResult response = authorizeIncomingTransfer.execute(command);

        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                TransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY
            );
    }
}
