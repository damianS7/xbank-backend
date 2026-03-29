package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAlias;
import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAliasCommand;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SetAccountAliasTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private SetAccountAlias setAccountAlias;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("should returns a BankingAccount with updated alias when valid request")
    void setAlias_WhenValidRequest_ReturnsAccountWithUpdatedAlias() {
        // given
        setUpContext(customer);

        SetAccountAliasCommand command = new SetAccountAliasCommand(
            bankingAccount.getId(),
            "account for savings"
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        setAccountAlias.execute(command);

        // then
        assertThat(bankingAccount.getAlias()).isEqualTo(command.alias());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }
}