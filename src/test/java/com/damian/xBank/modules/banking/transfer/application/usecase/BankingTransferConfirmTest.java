package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransferConfirmTest extends AbstractServiceTest {

    @InjectMocks
    private BankingTransferConfirm bankingTransferConfirm;

    @Mock
    private BankingTransferDomainService bankingTransferDomainService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    private Customer fromCustomer;
    private Customer toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        fromAccount = BankingAccount
                .create(fromCustomer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        toAccount = BankingAccount
                .create(toCustomer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("confirmTransfer a valid request should confirm a transfer")
    void confirmTransfer_ValidRequest_ReturnsConfirmedTransfer() {
        // given
        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
                .create(fromAccount, toAccount, BigDecimal.valueOf(100))
                .setId(1L)
                .setStatus(BankingTransferStatus.CONFIRMED)
                .setDescription("a gift!");

        BankingTransaction fromTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_TO,
                        fromAccount,
                        givenTransfer.getAmount()
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(givenTransfer.getDescription());

        BankingTransaction toTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        givenTransfer.getAmount()
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(givenTransfer.getDescription());

        givenTransfer.addTransaction(fromTransaction);
        givenTransfer.addTransaction(toTransaction);

        BankingTransferConfirmRequest request = new BankingTransferConfirmRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        when(bankingTransferDomainService.confirmTransfer(anyLong(), any())).thenReturn(givenTransfer);

        //        when(bankingAccountRepository.save(any(BankingAccount.class)))
        //                .thenAnswer(i -> i.getArgument(0));

        //        when(bankingTransferRepository.save(any(BankingTransfer.class)))
        //                .thenAnswer(i -> i.getArgument(0));

        doNothing().when(notificationPublisher).publish(any(NotificationEvent.class));

        // then
        bankingTransferConfirm
                .confirmTransfer(givenTransfer.getId(), request);

        verify(bankingAccountRepository, times(2)).save(any(BankingAccount.class));
        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
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

    // TODO

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

}