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
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Set;

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
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

    }

    @Test
    @DisplayName("should return a set containing all banking accounts from authenticated user")
    void getCustomerBankingAccounts_WhenValidRequest_ReturnsCustomerBankingAccounts() {
        // given
        setUpContext(customer);

        BankingAccount account = BankingAccountTestBuilder.builder()
            .withId(3L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335516")
            .build();

        GetAllUserAccountsQuery query = new GetAllUserAccountsQuery();

        // when
        when(bankingAccountRepository.findByUser_Id(anyLong())).thenReturn(
            Set.of(account)
        );

        GetAllUserAccountsResult result = getAllUserAccounts.execute(query);

        // then
        assertThat(result.accounts().size()).isEqualTo(1);
        verify(bankingAccountRepository, times(1)).findByUser_Id(anyLong());
    }
}