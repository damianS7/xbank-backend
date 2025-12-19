package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionOwnershipException;
import com.damian.xBank.modules.banking.transaction.infra.repository.BankingTransactionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BankingTransactionCardServiceTest extends AbstractServiceTest {

    @Mock
    private BankingTransactionAccountService bankingTransactionAccountService;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionCardService bankingTransactionCardService;

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create()
                .setOwner(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setId(11L)
                .setAssociatedBankingAccount(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("Should confirm pending transaction")
    void shouldConfirmTransaction() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = new BankingTransaction(customerBankingAccount);
        givenTransaction.setBankingCard(customerBankingCard);
        givenTransaction.setStatus(BankingTransactionStatus.PENDING);
        givenTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenTransaction.setAmount(BigDecimal.valueOf(1000));
        givenTransaction.setDescription("Amazon.com");

        when(bankingTransactionRepository.findById(anyLong())).thenReturn(Optional.of(givenTransaction));

        when(bankingTransactionRepository.save(any(BankingTransaction.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        // then
        bankingTransactionCardService.confirmTransaction(
                customerBankingCard.getId()
        );

        // then
        assertThat(givenTransaction).isNotNull();
        assertThat(givenTransaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(customerBankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should fail to confirm transaction when not owner")
    void shouldFailToConfirmTransactionWhenNotOwner() {
        // given
        Customer customerOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount customerOwnerBankingAccount = BankingAccount
                .create()
                .setOwner(customerOwner)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        BankingCard customerOwnerBankingCard = BankingCard
                .create()
                .setId(1L)
                .setAssociatedBankingAccount(customerOwnerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        setUpContext(customer);

        BankingTransaction givenTransaction = new BankingTransaction(customerOwnerBankingAccount);
        givenTransaction.setId(1L);
        givenTransaction.setBankingCard(customerOwnerBankingCard);
        givenTransaction.setStatus(BankingTransactionStatus.PENDING);
        givenTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenTransaction.setAmount(BigDecimal.valueOf(3000));
        givenTransaction.setDescription("Amazon.com");

        when(bankingTransactionRepository.findById(anyLong())).thenReturn(Optional.of(givenTransaction));

        // then
        BankingTransactionOwnershipException exception = assertThrows(
                BankingTransactionOwnershipException.class,
                () -> bankingTransactionCardService.confirmTransaction(givenTransaction.getId())
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }

    @Test
    @DisplayName("Should fail to confirm transaction when insufficient funds")
    void shouldFailToConfirmTransactionWhenInsufficientFunds() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = new BankingTransaction(customerBankingAccount);
        givenTransaction.setBankingCard(customerBankingCard);
        givenTransaction.setStatus(BankingTransactionStatus.PENDING);
        givenTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenTransaction.setAmount(BigDecimal.valueOf(3000));
        givenTransaction.setDescription("Amazon.com");

        when(bankingTransactionRepository.findById(anyLong())).thenReturn(Optional.of(givenTransaction));

        // then
        BankingCardInsufficientFundsException exception = assertThrows(
                BankingCardInsufficientFundsException.class,
                () -> bankingTransactionCardService.confirmTransaction(
                        customerBankingCard.getId()
                )
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
        assertThat(givenTransaction.getStatus()).isEqualTo(BankingTransactionStatus.PENDING);
        verify(bankingTransactionRepository, times(0)).save(any(BankingTransaction.class));
    }

}
