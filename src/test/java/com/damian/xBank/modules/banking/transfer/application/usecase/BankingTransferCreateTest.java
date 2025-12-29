package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BankingTransferCreateTest extends AbstractServiceTest {

    @Mock
    private BankingTransferDomainService bankingTransferDomainService;

    @InjectMocks
    private BankingTransferCreate bankingTransferCreate;

    @Mock
    private NotificationPublisher notificationPublisher;

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
    @DisplayName("createTransfer should successfully create a transfer when request is valid")
    void createTransfer_ValidRequest_ReturnsTransfer() {
        // given
        setUpContext(fromCustomer);

        BankingTransferRequest request = new BankingTransferRequest(
                fromAccount.getId(),
                toAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(100)
        );

        BankingTransfer givenTransfer = BankingTransfer
                .create(fromAccount, toAccount, BigDecimal.valueOf(100))
                .setId(1L)
                .setStatus(BankingTransferStatus.CONFIRMED)
                .setDescription("a gift!");

        BankingTransaction toTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        givenTransfer.getAmount()
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(givenTransfer.getDescription());

        givenTransfer.addTransaction(toTransaction);

        // when
        when(bankingAccountRepository.findById(fromAccount.getId())).thenReturn(
                Optional.of(fromAccount));

        when(bankingAccountRepository.findByAccountNumber(toAccount.getAccountNumber())).thenReturn(
                Optional.of(toAccount));

        doNothing().when(notificationPublisher).publish(any(NotificationEvent.class));

        when(bankingTransferDomainService.createTransfer(
                anyLong(),
                any(BankingAccount.class),
                any(BankingAccount.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenTransfer);

        when(bankingTransferRepository.save(any(BankingTransfer.class))).thenAnswer(
                i -> i.getArguments()[0]
        );

        BankingTransfer resultTransfer = bankingTransferCreate.createTransfer(request);

        // then
        assertThat(resultTransfer)
                .isNotNull()
                .extracting(
                        BankingTransfer::getId,
                        BankingTransfer::getAmount,
                        BankingTransfer::getStatus,
                        BankingTransfer::getDescription,
                        BankingTransfer::getCreatedAt
                )
                .containsExactly(
                        resultTransfer.getId(),
                        request.amount(),
                        BankingTransferStatus.CONFIRMED,
                        request.description(),
                        resultTransfer.getCreatedAt()
                );

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }

}