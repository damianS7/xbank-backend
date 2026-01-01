package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountActivateRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("should return active account when admin tries to activate suspended account")
    void execute_WhenSuspendedAccountActiveByAdmin_ReturnActiveAccount() {
        // given
        customer.setRole(UserAccountRole.ADMIN);
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        BankingAccountActivateRequest request = new BankingAccountActivateRequest();

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(i -> i.getArgument(0));

        BankingAccount result = bankingAccountActivate.execute(bankingAccount.getId(), request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(BankingAccount::getStatus)
                .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throws exception when customer tries to activate suspended account")
    void execute_WhenSuspendedAccountActiveByCustomer_ReturnSuspendedAccount() {
        // given
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        BankingAccountActivateRequest request = new BankingAccountActivateRequest();

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(i -> i.getArgument(0));

        BankingAccount result = bankingAccountActivate.execute(bankingAccount.getId(), request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(BankingAccount::getStatus)
                .isEqualTo(BankingAccountStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Should throws exception when trying to activate closed account")
    void execute_WhenClosedAccount_ThrowsException() {
        // given
        customer.setRole(UserAccountRole.ADMIN);
        setUpContext(customer);

        bankingAccount.setStatus(BankingAccountStatus.CLOSED);

        BankingAccountActivateRequest request = new BankingAccountActivateRequest();

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