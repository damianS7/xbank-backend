package com.damian.xBank.modules.banking.account;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountOperationService;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.service.BankingTransactionAccountService;
import com.damian.xBank.modules.user.account.account.model.UserAccount;
import com.damian.xBank.modules.user.customer.model.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingAccountOperationService bankingAccountOperationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionAccountService bankingTransactionAccountService;

    //
    //    @Mock
    //    private CustomerRepository customerRepository;
    //
    //    @Mock
    //    private Faker faker;
    //
    //    @Mock
    //    private Finance finance;
    //
    //    @Mock
    //    private BankingTransactionService bankingAccountOperationService;
    //

    @Test
    @DisplayName("Should deposit")
    void shouldDeposit() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenDepositAmount = BigDecimal.valueOf(3000);

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(2L);
        givenBankingAccount.setBalance(BigDecimal.ZERO);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        BankingTransaction givenBankingTransaction = new BankingTransaction(givenBankingAccount);
        givenBankingTransaction.setTransactionType(BankingTransactionType.DEPOSIT);
        givenBankingTransaction.setAmount(givenDepositAmount);

        BankingAccountDepositRequest depositRequest = new BankingAccountDepositRequest(
                BankingTransactionType.DEPOSIT,
                givenBankingAccount.getAccountNumber(),
                givenDepositAmount
        );

        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(
                givenBankingAccount));

        when(bankingTransactionAccountService.generateTransaction(
                any(BankingAccount.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransaction);

        when(bankingTransactionAccountService.persistTransaction(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        BankingTransaction transaction = bankingAccountOperationService.deposit(
                givenBankingAccount.getId(),
                depositRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getTransactionType()).isEqualTo(givenBankingTransaction.getTransactionType());
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(givenBankingAccount.getBalance()).isEqualTo(givenDepositAmount);
    }

    //    @Test
    //    @DisplayName("Should process transaction and transfer to")
    //    void shouldProcessTransactionRequestAndTransferTo() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        when(bankingAccountOperationService.createTransaction(
    //                any(BankingAccount.class),
    //                any(BankingTransactionType.class),
    //                any(BigDecimal.class),
    //                any(String.class)
    //        )).thenReturn(givenBankingTransactionA);
    //
    //        when(bankingAccountOperationService.persistTransaction(
    //                any(BankingTransaction.class)
    //        )).thenReturn(givenBankingTransactionA);
    //
    //        // then
    //        BankingTransaction transaction = bankingTransactionAccountService.processTransactionRequest(
    //                givenBankingAccountA.getId(),
    //                givenRequest
    //        );
    //
    //        // then
    //        assertThat(transaction).isNotNull();
    //        assertThat(givenBankingAccountA.getBalance()).isEqualTo(
    //                givenBalanceAccountA.subtract(givenTransferAmount)
    //        );
    //        assertThat(givenBankingAccountB.getBalance()).isEqualTo(
    //                givenBalanceAccountB.add(givenTransferAmount)
    //        );
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when insufficient funds")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenInsufficientFunds() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.BANK_ACCOUNT.INSUFFICIENT_FUNDS, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when insufficient funds")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenDifferentCurrencies() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setAccountCurrency(BankingAccountCurrency.USD);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.TRANSACTION.DIFFERENT_CURRENCY, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account number is null")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenAccountNumberIsNull() {
    //        // given
    //        //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber(null);
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.empty());
    //
    //        // then
    //        BankingAccountNotFoundException exception = assertThrows(
    //                BankingAccountNotFoundException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.USER_ACCOUNT.NOT_FOUND, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account is closed")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenAccountIsClosed() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setAccountStatus(BankingAccountStatus.CLOSED);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.BANK_ACCOUNT.CLOSED, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account is suspended")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenAccountIsSuspended() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setAccountStatus(BankingAccountStatus.SUSPENDED);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.USER_ACCOUNT.SUSPENDED, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account is closed")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenDestinyAccountIsClosed() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setAccountStatus(BankingAccountStatus.CLOSED);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.BANK_ACCOUNT.CLOSED, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account is suspended")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenDestinyAccountIsSuspended() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountStatus(BankingAccountStatus.SUSPENDED);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.USER_ACCOUNT.SUSPENDED, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer to same account")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenToSameAccount() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountA.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountA.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountA));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.BANK_ACCOUNT.SAME_DESTINATION, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when account its not yours")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenAccountItsNotYours() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customerB);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customer);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                RAW_PASSWORD
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.BANK_ACCOUNT.ACCESS_FORBIDDEN, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should process transaction and fail to transfer when password is wrong")
    //    void shouldProcessTransactionRequestAndFailToTransferWhenPasswordIsWrong() {
    //        // given
    //        setUpContext(customer);
    //        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
    //        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
    //
    //        BankingAccount givenBankingAccountA = new BankingAccount(customer);
    //        givenBankingAccountA.setId(2L);
    //        givenBankingAccountA.setBalance(givenBalanceAccountA);
    //        givenBankingAccountA.setAccountNumber("US9900001111112233334444");
    //
    //        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
    //        givenBankingAccountB.setId(5L);
    //        givenBankingAccountB.setBalance(givenBalanceAccountB);
    //        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
    //        givenBankingTransactionA.setAmount(givenTransferAmount);
    //
    //        BankingAccountTransactionRequest givenRequest = new BankingAccountTransactionRequest(
    //                givenBankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "a gift!",
    //                BigDecimal.valueOf(500),
    //                "WRONG_PASSWORD"
    //        );
    //
    //        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
    //                givenBankingAccountA));
    //        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
    //                .thenReturn(Optional.of(givenBankingAccountB));
    //
    //        // then
    //        PasswordMismatchException exception = assertThrows(
    //                PasswordMismatchException.class,
    //                () -> bankingTransactionAccountService.processTransactionRequest(
    //                        givenBankingAccountA.getId(),
    //                        givenRequest
    //                )
    //        );
    //
    //        // then
    //        assertEquals(PasswordMismatchException.PASSWORD_MISMATCH, exception.getMessage());
    //    }

}