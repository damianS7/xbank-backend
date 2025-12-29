package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRejectRequest;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.service.NotificationService;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BankingTransferRejectTest extends AbstractServiceTest {

    @InjectMocks
    private BankingTransferReject bankingTransferReject;

    @Mock
    private BankingTransferService bankingTransferService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

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
                .setAccountType(BankingAccountType.SAVINGS)
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
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("rejectTransfer with a valid request should reject a transfer")
    void rejectTransfer_ValidRequest_ReturnsRejectedTransfer() {
        // given
        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
                .create(fromAccount, toAccount, BigDecimal.valueOf(100))
                .setId(1L)
                .setStatus(BankingTransferStatus.REJECTED)
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

        BankingTransferRejectRequest request = new BankingTransferRejectRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        when(bankingTransferService.reject(anyLong(), any())).thenReturn(givenTransfer);

        //        when(bankingTransferRepository.save(any(BankingTransfer.class)))
        //                .thenAnswer(i -> i.getArgument(0));

        doNothing().when(notificationService).publish(any(NotificationEvent.class));

        // then
        bankingTransferReject
                .rejectTransfer(givenTransfer.getId(), request);

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }
}