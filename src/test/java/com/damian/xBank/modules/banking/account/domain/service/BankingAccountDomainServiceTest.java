package com.damian.xBank.modules.banking.account.domain.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.service.BankingAccountNumberGenerator;
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
import static org.mockito.Mockito.when;

public class BankingAccountDomainServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountNumberGenerator bankingAccountNumberGenerator;

    @InjectMocks
    private BankingAccountDomainService bankingAccountDomainService;

    private User user;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withEmail("customer@demo.com")
                              .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                              .build();

        bankingAccount = BankingAccount
                .create(user)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        user.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("Should create a BankingAccount for logged customer")
    void createAccount_WhenValidRequest_ReturnsBankingAccount() {
        // given
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when
        when(bankingAccountNumberGenerator.generate())
                .thenReturn("ES1234567890123456789012");

        BankingAccount result = bankingAccountDomainService.createAccount(
                user,
                request.type(),
                request.currency()
        );

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        BankingAccount::getCurrency,
                        BankingAccount::getType
                ).containsExactly(
                        request.currency(),
                        request.type()
                );

        assertThat(result.getAccountNumber())
                .isNotBlank()
                .hasSize(24);
    }
}
