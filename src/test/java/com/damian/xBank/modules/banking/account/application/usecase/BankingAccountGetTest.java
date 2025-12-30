package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class BankingAccountGetTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingAccountGet bankingAccountGet;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        BankingAccount account1 = BankingAccount
                .create(customer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        BankingAccount account2 = BankingAccount
                .create(customer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334411");

        BankingAccount account3 = BankingAccount
                .create(customer)
                .setId(3L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334412");

        customer.setBankingAccounts(Set.of(account1, account2, account3));
    }

    @Test
    @DisplayName("execute should return banking accounts for a specific customer")
    void execute_Valid_ReturnsCustomerBankingAccounts() {
        // given
        setUpContext(customer);

        // when
        when(bankingAccountRepository.findByCustomer_Id(anyLong())).thenReturn(
                customer.getBankingAccounts()
        );

        Set<BankingAccount> result = bankingAccountGet.execute();

        // then
        assertThat(result.size()).isEqualTo(customer.getBankingAccounts().size());
        verify(bankingAccountRepository, times(1)).findByCustomer_Id(anyLong());
    }
}