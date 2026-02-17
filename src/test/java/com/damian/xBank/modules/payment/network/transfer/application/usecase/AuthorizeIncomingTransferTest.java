package com.damian.xBank.modules.payment.network.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.IncomingTransferAuthorizationResponse;
import com.damian.xBank.modules.payment.network.transfer.domain.IncomingTransferAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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

public class AuthorizeIncomingTransferTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private AuthorizeIncomingTransfer authorizeIncomingTransfer;

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
            .setId(2L)
            .setCurrency(BankingAccountCurrency.EUR)
            .setType(BankingAccountType.SAVINGS)
            .setAccountNumber("US9900001111112233334444");
    }

    @Test
    void authorizeIncomingTransfer_WhenValidRequest_ReturnsAuthorizedResponse() {
        // given
        IncomingTransferAuthorizationRequest request = new IncomingTransferAuthorizationRequest(
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);

        assertThat(response)
            .isNotNull()
            .extracting(
                IncomingTransferAuthorizationResponse::status
            ).isEqualTo(
                IncomingTransferAuthorizationStatus.AUTHORIZED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountNotFound_ReturnsRejectedResponse() {
        // given
        IncomingTransferAuthorizationRequest request = new IncomingTransferAuthorizationRequest(
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.empty());

        // then
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);

        assertThat(response)
            .isNotNull()
            .extracting(
                IncomingTransferAuthorizationResponse::status,
                IncomingTransferAuthorizationResponse::rejectionReason
            ).containsExactly(
                IncomingTransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_NOT_FOUND
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountSuspended_ReturnsRejectedResponse() {
        // given
        IncomingTransferAuthorizationRequest request = new IncomingTransferAuthorizationRequest(
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR"
        );

        bankingAccount.suspend();

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);

        assertThat(response)
            .isNotNull()
            .extracting(
                IncomingTransferAuthorizationResponse::status,
                IncomingTransferAuthorizationResponse::rejectionReason
            ).containsExactly(
                IncomingTransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_SUSPENDED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountClosed_ReturnsRejectedResponse() {
        // given
        IncomingTransferAuthorizationRequest request = new IncomingTransferAuthorizationRequest(
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR"
        );

        bankingAccount.close();

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);

        assertThat(response)
            .isNotNull()
            .extracting(
                IncomingTransferAuthorizationResponse::status,
                IncomingTransferAuthorizationResponse::rejectionReason
            ).containsExactly(
                IncomingTransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_CLOSED
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenCurrencyMismatch_ReturnsRejectedResponse() {
        // given
        bankingAccount.setCurrency(BankingAccountCurrency.EUR);

        IncomingTransferAuthorizationRequest request = new IncomingTransferAuthorizationRequest(
            bankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "USD"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.of(bankingAccount));

        // then
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);

        assertThat(response)
            .isNotNull()
            .extracting(
                IncomingTransferAuthorizationResponse::status,
                IncomingTransferAuthorizationResponse::rejectionReason
            ).containsExactly(
                IncomingTransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY
            );
    }
}
