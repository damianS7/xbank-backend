package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.shared.AbstractServiceTest;

public class BankingTransactionAccountServiceTest extends AbstractServiceTest {

    // TODO
    // should createTransaction
    // should generateTransaction

    //    @Test
    //    @DisplayName("It should create transaction.")
    //    void shouldCreateTransaction() {
    //        // given
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //        String givenDescription = "Account deposit";
    //        BigDecimal givenAmount = BigDecimal.valueOf(100);
    //        BankingTransactionType givenTransactionType = BankingTransactionType.DEPOSIT;
    //
    //        // when
    //        BankingTransaction createdBankingTransaction = bankingTransactionService.createTransaction(
    //                givenBankingAccount,
    //                givenTransactionType,
    //                givenAmount,
    //                givenDescription
    //        );
    //
    //        // then
    //        assertThat(createdBankingTransaction).isNotNull();
    //        assertThat(createdBankingTransaction.getAmount()).isEqualTo(givenAmount);
    //        assertThat(createdBankingTransaction.getTransactionType()).isEqualTo(givenTransactionType);
    //        assertThat(createdBankingTransaction.getDescription()).isEqualTo(givenDescription);
    //    }
    //
    //    @Test
    //    @DisplayName("It should persist transaction.")
    //    void shouldPersistTransaction() {
    //        // given
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //
    //        BankingTransaction givenBankingTransaction = new BankingTransaction(givenBankingAccount);
    //        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        givenBankingTransaction.setTransactionType(BankingTransactionType.DEPOSIT);
    //        givenBankingTransaction.setDescription("Account deposit");
    //
    //        // when
    //        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
    //                .thenReturn(givenBankingTransaction);
    //        BankingTransaction createdBankingTransaction = bankingTransactionService.persistTransaction(
    //                givenBankingTransaction
    //        );
    //
    //        // then
    //        assertThat(createdBankingTransaction).isNotNull();
    //        assertThat(createdBankingTransaction.getAmount()).isEqualTo(givenBankingTransaction.getAmount());
    //        assertThat(createdBankingTransaction.getTransactionType()).isEqualTo(givenBankingTransaction.getTransactionType());
    //        assertThat(createdBankingTransaction.getDescription()).isEqualTo(givenBankingTransaction.getDescription());
    //        assertThat(givenBankingAccount.getAccountTransactions().size()).isEqualTo(1);
    //        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    //    }
    //

    //
    //    @Test
    //    @DisplayName("It should update transaction status.")
    //    void shouldUpdateTransactionStatus() {
    //        // given
    //        setUpContext(customerAdmin);
    //
    //        BankingTransactionUpdateStatusRequest request = new BankingTransactionUpdateStatusRequest(
    //                BankingTransactionStatus.COMPLETED
    //        );
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //
    //        BankingAccount bankingAccount = new BankingAccount(customerA);
    //        bankingAccount.setAccountNumber(accountNumber);
    //
    //        BankingTransaction bankingTransaction = new BankingTransaction();
    //        bankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        bankingTransaction.setTransactionType(BankingTransactionType.CARD_CHARGE);
    //        bankingTransaction.setDescription("Amazon.com");
    //        bankingTransaction.setTransactionStatus(BankingTransactionStatus.PENDING);
    //
    //        bankingTransaction.setAssociatedBankingAccount(bankingAccount);
    //        bankingAccount.addAccountTransaction(bankingTransaction);
    //
    //        bankingAccountRepository.save(bankingAccount);
    //
    //        // when
    //        when(bankingTransactionRepository.findById(bankingTransaction.getId())).thenReturn(Optional.of(
    //                bankingTransaction));
    //        when(bankingTransactionRepository.save(any(BankingTransaction.class))).thenReturn(bankingTransaction);
    //
    //        BankingTransaction savedTransaction =
    //                bankingTransactionService.updateTransactionStatus(bankingTransaction.getId(), request);
    //
    //        // then
    //        assertThat(savedTransaction.getTransactionStatus()).isEqualTo(request.transactionStatus());
    //        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    //    }
    //
    //    @Test
    //    @DisplayName("It should not update transaction status when you are not admin.")
    //    void shouldNotUpdateTransactionStatusWhenYouAreNotAdmin() {
    //        // given
    //        setUpContext(customerA);
    //
    //        BankingTransactionUpdateStatusRequest request = new BankingTransactionUpdateStatusRequest(
    //                BankingTransactionStatus.COMPLETED
    //        );
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //
    //        BankingAccount bankingAccount = new BankingAccount(customerA);
    //        bankingAccount.setAccountNumber(accountNumber);
    //
    //        BankingTransaction bankingTransaction = new BankingTransaction();
    //        bankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        bankingTransaction.setTransactionType(BankingTransactionType.CARD_CHARGE);
    //        bankingTransaction.setDescription("Amazon.com");
    //        bankingTransaction.setTransactionStatus(BankingTransactionStatus.PENDING);
    //
    //        bankingTransaction.setAssociatedBankingAccount(bankingAccount);
    //        bankingAccount.addAccountTransaction(bankingTransaction);
    //
    //        bankingAccountRepository.save(bankingAccount);
    //
    //        // when
    //        BankingTransactionAuthorizationException exception = assertThrows(
    //                BankingTransactionAuthorizationException.class,
    //                () -> bankingTransactionService.updateTransactionStatus(bankingTransaction.getId(), request)
    //        );
    //
    //        // then
    //        assertTrue(exception.getMessage().contains(
    //                Exceptions.AUTH.NOT_ADMIN
    //        ));
    //    }
}