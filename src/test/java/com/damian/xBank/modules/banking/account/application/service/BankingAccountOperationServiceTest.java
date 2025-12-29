package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class BankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingAccountOperationService bankingAccountOperationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Test
    @DisplayName("Should fail to transfer when account is not found")
    void shouldFailToTransferWhenAccountIsNotFound() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setCurrency(BankingAccountCurrency.USD);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingTransferRequest transferRequest = new BankingTransferRequest(
                fromCustomerBankingAccount.getId(),
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(1)
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.empty());

        // then
        //        BankingAccountNotFoundException exception = assertThrows(
        //                BankingAccountNotFoundException.class,
        //                () -> bankingAccountOperationService.transfer(
        //                        fromCustomerBankingAccount,
        //                        toCustomerBankingAccount,
        //                        transferRequest.amount(),
        //                        transferRequest.description()
        //                )
        //        );

        // then
        //        assertEquals(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND, exception.getMessage());
    }

    // TODO move to BankingAccountOperationServiceTest
    //    @Test
    //    @DisplayName("Should fail to confirm transfer when password is wrong")
    //    void shouldFailToConfirmTransferWhenPasswordIsWrong() {
    //        // given
    //        Customer fromCustomer = Customer.create(
    //                UserAccount.create()
    //                           .setId(1L)
    //                           .setEmail("fromCustomer@demo.com")
    //                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
    //        ).setId(1L);
    //
    //        setUpContext(fromCustomer);
    //
    //        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
    //        fromCustomerBankingAccount.setId(2L);
    //        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
    //        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");
    //
    //        Customer toCustomer = Customer.create(
    //                UserAccount.create()
    //                           .setId(2L)
    //                           .setEmail("customerB@demo.com")
    //                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
    //        ).setId(2L);
    //
    //        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
    //        toCustomerBankingAccount.setId(5L);
    //        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(100));
    //        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");
    //
    //        BankingTransferConfirmRequest transferRequest = new BankingTransferConfirmRequest(
    //                "WRONG_PASSWORD"
    //        );
    //
    //        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
    //                fromCustomerBankingAccount));
    //
    //        //        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
    //        //                .thenReturn(Optional.of(toCustomerBankingAccount));
    //
    //        // then
    //        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
    //                UserAccountInvalidPasswordConfirmationException.class,
    //                //                () -> bankingTransferService.confirm(transfer.getId(), transferRequest)
    //                () -> bankingTransferService.confirm(transfer)
    //        );
    //
    //        // then
    //        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD);
    //    }
}