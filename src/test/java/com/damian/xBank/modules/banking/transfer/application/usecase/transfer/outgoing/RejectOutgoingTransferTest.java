package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.reject.RejectOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.reject.RejectOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class RejectOutgoingTransferTest extends AbstractServiceTest {

    @InjectMocks
    private RejectOutgoingTransfer rejectOutgoingTransfer;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

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
            .withPassword(RAW_PASSWORD)
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
    @DisplayName("should return rejected transfer")
    void rejectTransfer_WhenValidRequest_ReturnsRejectedTransfer() {
        // given
        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
            .create(fromAccount, toAccount, BigDecimal.valueOf(100))
            .setId(1L)
            .setStatus(BankingTransferStatus.REJECTED)
            .setDescription("a gift!");

        RejectOutgoingTransferCommand command = new RejectOutgoingTransferCommand(
            givenTransfer.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        // then
        rejectOutgoingTransfer.execute(command);
    }
}