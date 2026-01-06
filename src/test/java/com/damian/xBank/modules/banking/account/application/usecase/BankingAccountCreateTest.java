package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.factory.BankingAccountFactory;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.account.infrastructure.service.BankingAccountNumberGenerator;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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
import static org.mockito.Mockito.*;

public class BankingAccountCreateTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankingAccountNumberGenerator bankingAccountNumberGenerator;

    @Mock
    private BankingAccountDomainService bankingAccountDomainService;

    @InjectMocks
    private BankingAccountCreate bankingAccountCreate;

    private User user;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withEmail("customer@demo.com")
                              .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                              .build();

        bankingAccount = BankingAccountFactory.createFor(user);
    }

    @Test
    @DisplayName("should returns a newly created BankingAccount when valid request")
    void accountCreate_WhenValidRequest_ReturnsCreatedAccount() {
        // given
        setUpContext(user);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
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
    @DisplayName("should throws exception when user not found")
    void accountCreate_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(user);

        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bankingAccountCreate.execute(request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_NOT_FOUND);
        verify(bankingAccountRepository, times(0)).save(any(BankingAccount.class));
    }
}
