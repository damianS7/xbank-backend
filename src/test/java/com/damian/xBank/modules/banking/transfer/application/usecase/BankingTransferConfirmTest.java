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
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.aCustomer()
                                      .withId(1L)
                                      .withEmail("fromCustomer@demo.com")
                                      .withPassword(RAW_PASSWORD)
                                      .build();

        fromAccount = BankingAccount
                .create(fromCustomer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        toCustomer = UserTestBuilder.aCustomer()
                                    .withId(2L)
                                    .withEmail("toCustomer@demo.com")
                                    .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                    .build();

        toAccount = BankingAccount
                .create(toCustomer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("should return confirmed transfer when request is valid")
    void confirmTransfer_WhenValidRequest_ReturnsConfirmedTransfer() {
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
                .execute(givenTransfer.getId(), request);

        verify(bankingAccountRepository, times(2)).save(any(BankingAccount.class));
        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }

    @Test
    @DisplayName("should throw exception when invalid password")
    void confirmTransfer_WhenInvalidPassword_ThrowsException() {
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
                "WRONG PASSWORD"
        );

        // when
        // then
        UserInvalidPasswordConfirmationException exception = assertThrows(
                UserInvalidPasswordConfirmationException.class,
                () -> bankingTransferConfirm.execute(givenTransfer.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_INVALID_PASSWORD);
    }
}