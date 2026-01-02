package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
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
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

public class BankingTransactionGetTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionGet bankingTransactionGet;

    private Customer customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");
    }

    @Test
    @DisplayName("should return a transaction when exists and is owner")
    void getTransaction_WhenExistsAndIsOwner_ReturnsTransaction() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = BankingTransaction
                .create(
                        BankingTransactionType.DEPOSIT,
                        customerBankingAccount,
                        BigDecimal.valueOf(100)
                )
                .setId(1L)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(givenTransaction.getId()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransaction retrievedTransaction = bankingTransactionGet
                .execute(givenTransaction.getId());

        // then
        assertThat(retrievedTransaction).isNotNull();
        assertThat(retrievedTransaction.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(retrievedTransaction.getType()).isEqualTo(givenTransaction.getType());
        assertThat(retrievedTransaction.getDescription()).isEqualTo(givenTransaction.getDescription());
    }

    @Test
    @DisplayName("should throw exception when transaction not exists")
    void getTransaction_WhenNotExists_ThrowsException() {
        // given
        setUpContext(customer);

        // when
        when(bankingTransactionRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        BankingTransactionNotFoundException exception = assertThrows(
                BankingTransactionNotFoundException.class,
                () -> bankingTransactionGet.execute(1L)

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
        setUpContext(customer);

        Customer otherCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("otherCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        customerBankingAccount.setOwner(otherCustomer);

        BankingTransaction givenTransaction = BankingTransaction
                .create(
                        BankingTransactionType.DEPOSIT,
                        customerBankingAccount,
                        BigDecimal.valueOf(100)
                )
                .setId(1L)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransactionNotOwnerException exception = assertThrows(
                BankingTransactionNotOwnerException.class,
                () -> bankingTransactionGet.execute(givenTransaction.getId())

        );
        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }
}