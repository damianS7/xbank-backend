package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.cqrs.command.CreateBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.cqrs.result.BankingAccountResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountNumberGenerator;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.BankingAccountTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateBankingAccountTest extends AbstractServiceTest {

    @Mock private BankingAccountRepository bankingAccountRepository;

    @Mock private UserRepository userRepository;

    @Mock private BankingAccountNumberGenerator bankingAccountNumberGenerator;

    @Mock private BankingAccountDomainService bankingAccountDomainService;

    @InjectMocks private CreateBankingAccount createBankingAccount;

    private User user;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestFactory.defaultAccount(user);
    }

    @Test
    @DisplayName("should returns a newly created BankingAccount when valid request")
    void accountCreate_WhenValidRequest_ReturnsCreatedAccount() {
        // given
        setUpContext(user);

        CreateBankingAccountCommand command = new CreateBankingAccountCommand(
            BankingAccountType.SAVINGS,
            BankingAccountCurrency.EUR
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(bankingAccountDomainService.createAccount(
            any(User.class),
            any(BankingAccountType.class),
            any(BankingAccountCurrency.class)
        )).thenReturn(bankingAccount);

        when(bankingAccountRepository.save(any(BankingAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankingAccountResult result = createBankingAccount.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accountCurrency()).isEqualTo(command.currency());
        assertThat(result.accountType()).isEqualTo(command.type());
        assertThat(result.accountNumber().length()).isEqualTo(24);
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(0));
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("should throws exception when user not found")
    void accountCreate_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(user);

        CreateBankingAccountCommand command = new CreateBankingAccountCommand(
            BankingAccountType.SAVINGS,
            BankingAccountCurrency.EUR
        );

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> createBankingAccount.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_NOT_FOUND);
        verify(bankingAccountRepository, times(0)).save(any(BankingAccount.class));
    }
}
