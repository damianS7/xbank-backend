package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.account.infrastructure.service.BankingAccountNumberGenerator;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingAccountCreateTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankingAccountNumberGenerator bankingAccountNumberGenerator;

    private BankingAccountDomainService bankingAccountDomainService;

    @InjectMocks
    private BankingAccountCreate bankingAccountCreate;

    private Customer customer;

    @BeforeEach
    void setUp() {
        // TODO review this injection
        bankingAccountDomainService =
                new BankingAccountDomainService(bankingAccountNumberGenerator);

        bankingAccountCreate = new BankingAccountCreate(
                bankingAccountDomainService,
                bankingAccountRepository,
                customerRepository,
                authenticationContext
        );

        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);
    }

    @Test
    @DisplayName("should returns a newly created BankingAccount when valid request")
    void accountCreate_WhenValidRequest_ReturnsCreatedAccount() {
        // given
        setUpContext(customer);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        when(bankingAccountNumberGenerator.generate())
                .thenReturn("US9900001111112233334444");

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingAccount result = bankingAccountCreate.execute(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrency()).isEqualTo(request.currency());
        assertThat(result.getType()).isEqualTo(request.type());
        assertThat(result.getAccountNumber().length()).isEqualTo(24);
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("should throws exception when customer not found")
    void accountCreate_WhenCustomerNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> bankingAccountCreate.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.CUSTOMER_NOT_FOUND);
        verify(bankingAccountRepository, times(0)).save(any(BankingAccount.class));
    }
}
