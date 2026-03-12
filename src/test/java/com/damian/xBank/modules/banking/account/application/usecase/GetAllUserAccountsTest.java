package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccounts;
import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccountsQuery;
import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccountsResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllUserAccountsTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private GetAllUserAccounts getAllUserAccounts;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        BankingAccount account1 = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
        ;

        BankingAccount account2 = BankingAccountTestBuilder.builder()
            .withId(2L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335511")
            .build();
        ;

        BankingAccount account3 = BankingAccountTestBuilder.builder()
            .withId(3L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335516")
            .build();

        customer.addBankingAccount(account1);
        customer.addBankingAccount(account2);
        customer.addBankingAccount(account3);
    }

    @Test
    @DisplayName("should return a set containing all banking accounts from authenticated user")
    void getCustomerBankingAccounts_WhenValidRequest_ReturnsCustomerBankingAccounts() {
        // given
        setUpContext(customer);

        GetAllUserAccountsQuery query = new GetAllUserAccountsQuery();

        // when
        when(bankingAccountRepository.findByUser_Id(anyLong())).thenReturn(
            customer.getBankingAccounts()
        );

        GetAllUserAccountsResult result = getAllUserAccounts.execute(query);

        // then
        assertThat(result.accounts().size()).isEqualTo(customer.getBankingAccounts().size());
        verify(bankingAccountRepository, times(1)).findByUser_Id(anyLong());
    }
}