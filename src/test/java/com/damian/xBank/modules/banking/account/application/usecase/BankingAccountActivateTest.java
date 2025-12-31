package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BankingAccountActivateTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private BankingAccountActivate bankingAccountActivate;

    private Customer customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        bankingAccount = BankingAccount
                .create(customer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("Should returns ACTIVE account when status is PENDING_ACTIVATION")
    void execute_WhenPendingActivationAccount_ReturnsActiveAccount() {
        // given
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.PENDING_ACTIVATION);

        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class))).thenAnswer(
                i -> i.getArgument(0)
        );

        BankingAccount result = bankingAccountActivate.execute(bankingAccount.getId(), request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(BankingAccount::getStatus)
                .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throws exception when trying to activate closed account")
    void execute_WhenClosedAccount_ThrowsException() {
        // given
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.CLOSED);

        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        BankingAccountStatusTransitionException exception = assertThrows(
                BankingAccountStatusTransitionException.class,
                () -> bankingAccountActivate.execute(bankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, exception.getMessage());
    }

    @Test
    @DisplayName("Should throws exception when trying to activate suspended account")
    void execute_WhenSuspendedAccount_ThrowsException() {
        // given
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);
        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        BankingAccountStatusTransitionException exception = assertThrows(
                BankingAccountStatusTransitionException.class,
                () -> bankingAccountActivate.execute(bankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, exception.getMessage());
    }
}