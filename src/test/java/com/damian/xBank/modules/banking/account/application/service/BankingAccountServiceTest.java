package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.infra.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import net.datafaker.Faker;
import net.datafaker.providers.base.Country;
import net.datafaker.providers.base.Finance;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingAccountServiceTest extends AbstractServiceTest {

    @Mock
    private Faker faker;

    @Mock
    private Finance finance;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingAccountService bankingAccountService;

    @Test
    @DisplayName("Should generate a unique account number")
    void shouldGenerateUniqueAccountNumber() {
        // given
        String givenCountryCode = "US";
        String givenAccountNumber = "9900001111112233334444";
        Country country = Mockito.mock(Country.class);
        Number number = Mockito.mock(Number.class);

        // when
        when(faker.country()).thenReturn(country);
        when(faker.number()).thenReturn(number);
        when(faker.number().digits(22)).thenReturn(givenAccountNumber);
        when(faker.country().countryCode2()).thenReturn(givenCountryCode);
        String generatedAccountNumber = bankingAccountService.generateAccountNumber();

        // then
        assertThat(generatedAccountNumber.length()).isEqualTo(24);
    }

    @Test
    @DisplayName("Should banking accounts for a specific customer")
    void shouldGetCustomerBankingAccounts() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        Set<BankingAccount> bankingAccounts = new HashSet<>();
        BankingAccount bankingAccountA = new BankingAccount(customer);
        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountA.setAccountNumber("US99 0000 1111 1122 3333 4444");
        bankingAccounts.add(bankingAccountA);

        BankingAccount bankingAccountB = new BankingAccount(customer);
        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountB.setAccountNumber("US99 0000 1111 1122 3333 6666");
        bankingAccounts.add(bankingAccountB);

        // when
        when(bankingAccountRepository.findByCustomer_Id(anyLong())).thenReturn(bankingAccounts);
        bankingAccounts = bankingAccountService.getLoggedCustomerBankingAccounts();

        // then
        assertThat(bankingAccounts.size()).isEqualTo(2);
        verify(bankingAccountRepository, times(1)).findByCustomer_Id(anyLong());
    }

    @Test
    @DisplayName("Should create a BankingAccount for logged customer")
    void shouldCreateBankingAccount() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        Country country = Mockito.mock(Country.class);
        Number number = Mockito.mock(Number.class);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");
        givenBankingAccount.setAccountCurrency(request.currency());
        givenBankingAccount.setAccountType(request.type());

        // when
        when(faker.country()).thenReturn(country);
        when(faker.number()).thenReturn(number);
        when(faker.country().countryCode2()).thenReturn("US");
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingAccountService.createBankingAccount(request);

        // then
        assertThat(givenBankingAccount).isNotNull();
        assertThat(givenBankingAccount.getAccountCurrency()).isEqualTo(request.currency());
        assertThat(givenBankingAccount.getAccountType()).isEqualTo(request.type());
        assertThat(givenBankingAccount.getAccountNumber().length()).isEqualTo(24);
        assertThat(givenBankingAccount.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should not create a BankingAccount when customer not found")
    void shouldFailToCreateBankingAccountWhenCustomerNotFound() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountCurrency(request.currency());
        givenBankingAccount.setAccountType(request.type());
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> bankingAccountService.createBankingAccount(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.CUSTOMER.NOT_FOUND);
        verify(bankingAccountRepository, times(0)).save(any(BankingAccount.class));
    }
}
