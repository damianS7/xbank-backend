package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionDetailResult;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.byid.GetTransaction;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.byid.GetTransactionQuery;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestBuilder;
import com.damian.xBank.test.utils.UserTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

public class GetTransactionTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private GetTransaction getTransaction;

    private User customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        customerBankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("should return a transaction when exists and is owner")
    void getTransaction_WhenExistsAndIsOwner_ReturnsTransaction() {
        // given
        setUpContext(customer);

        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withId(1L)
            .withAccount(customerBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withStatus(BankingTransactionStatus.PENDING)
            .withType(BankingTransactionType.DEPOSIT)
            .withDescription("Deposit transaction")
            .build();

        GetTransactionQuery query = new GetTransactionQuery(transaction.getId());

        // when
        when(bankingTransactionRepository.findById(transaction.getId()))
            .thenReturn(Optional.of(transaction));

        BankingTransactionDetailResult result = getTransaction.execute(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(transaction.getAmount());
        assertThat(result.type()).isEqualTo(transaction.getType());
        assertThat(result.description()).isEqualTo(transaction.getDescription());
    }

    @Test
    @DisplayName("should throw exception when transaction not exists")
    void getTransaction_WhenNotExists_ThrowsException() {
        // given
        setUpContext(customer);
        GetTransactionQuery query = new GetTransactionQuery(99L);

        // when
        when(bankingTransactionRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        BankingTransactionNotFoundException exception = assertThrows(
            BankingTransactionNotFoundException.class,
            () -> getTransaction.execute(query)
        );

        // then
        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when not owner of transaction")
    void getTransaction_WhenNotOwner_ThrowsException() {
        // given
        User otherCustomer = UserTestBuilder.builder()
            .withId(2L)
            .withEmail("otherCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        setUpContext(otherCustomer);

        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withId(1L)
            .withAccount(customerBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withStatus(BankingTransactionStatus.PENDING)
            .withType(BankingTransactionType.DEPOSIT)
            .withDescription("Deposit transaction")
            .build();

        GetTransactionQuery query = new GetTransactionQuery(transaction.getId());

        // when
        when(bankingTransactionRepository.findById(anyLong()))
            .thenReturn(Optional.of(transaction));

        BankingTransactionNotOwnerException exception = assertThrows(
            BankingTransactionNotOwnerException.class,
            () -> getTransaction.execute(query)

        );
        // then
        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }
}