package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class BankingAccountActivationTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingAccountActivation bankingAccountActivation;

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

        customer.setBankingAccounts(Set.of(bankingAccount));
    }

    // TODO should close

    @Test
    @DisplayName("Should not open BankingAccount When its closed")
    void shouldFailToOpenAccountWhenClosed() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setStatus(BankingAccountStatus.CLOSED);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccountActivation.execute(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_CLOSED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not open BankingAccount When its suspended")
    void shouldFailToOpenAccountWhenSuspended() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountActivation.execute(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_SUSPENDED, exception.getMessage());
    }


}