package com.damian.xBank.modules.banking.account.domain.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingAccountDomainServiceTest extends AbstractServiceTest {

    @Mock
    private Faker faker;

    @Mock
    private Finance finance;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingAccountDomainService bankingAccountDomainService;

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
        //        String generatedAccountNumber = bankingAccountService.generateAccountNumber();

        // then
        //        assertThat(generatedAccountNumber.length()).isEqualTo(24);
    }

    @Test
    @DisplayName("Should create a BankingAccount for logged customer")
    void shouldCreateBankingAccount() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
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
        givenBankingAccount.setCurrency(request.currency());
        givenBankingAccount.setType(request.type());

        // when
        when(faker.country()).thenReturn(country);
        when(faker.number()).thenReturn(number);
        when(faker.country().countryCode2()).thenReturn("US");
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //        bankingAccountService.createBankingAccount(request);

        // then
        assertThat(givenBankingAccount).isNotNull();
        assertThat(givenBankingAccount.getCurrency()).isEqualTo(request.currency());
        assertThat(givenBankingAccount.getType()).isEqualTo(request.type());
        assertThat(givenBankingAccount.getAccountNumber().length()).isEqualTo(24);
        assertThat(givenBankingAccount.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }
}
