package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateOutgoingTransferTest extends AbstractServiceTest {

    @InjectMocks
    private CreateOutgoingTransfer createOutgoingTransfer;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("fromCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        fromAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233334444")
            .build();
        ;

        toCustomer = UserTestBuilder.builder()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(2L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return created transfer when request is valid")
    void createOutgoingTransfer_WhenValidRequest_ReturnsTransfer() {
        // given
        setUpContext(fromCustomer);

        CreateOutgoingTransferCommand command = new CreateOutgoingTransferCommand(
            fromAccount.getId(),
            toAccount.getAccountNumber(),
            "a gift!",
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingAccountRepository.findById(fromAccount.getId())).thenReturn(
            Optional.of(fromAccount));

        when(bankingAccountRepository.findByAccountNumber(toAccount.getAccountNumber())).thenReturn(
            Optional.of(toAccount));

        when(outgoingTransferRepository.save(any(OutgoingTransfer.class))).thenAnswer(
            i -> i.getArguments()[0]
        );

        CreateOutgoingTransferResult resultTransfer = createOutgoingTransfer.execute(command);

        // then
        assertThat(resultTransfer)
            .isNotNull()
            .extracting(
                CreateOutgoingTransferResult::id,
                CreateOutgoingTransferResult::amount,
                CreateOutgoingTransferResult::status,
                CreateOutgoingTransferResult::description,
                CreateOutgoingTransferResult::createdAt
            )
            .containsExactly(
                resultTransfer.id(),
                command.amount(),
                OutgoingTransferStatus.PENDING,
                command.description(),
                resultTransfer.createdAt()
            );

        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));
    }

    @Test
    void createOutgoingTransfer_WhenToAccountNull_ReturnsExternalTransfer() {
        // given
        setUpContext(fromCustomer);

        CreateOutgoingTransferCommand command = new CreateOutgoingTransferCommand(
            fromAccount.getId(),
            toAccount.getAccountNumber(),
            "a gift!",
            BigDecimal.valueOf(100)
        );

        // when
        when(bankingAccountRepository.findById(fromAccount.getId())).thenReturn(
            Optional.of(fromAccount));

        when(bankingAccountRepository.findByAccountNumber(toAccount.getAccountNumber()))
            .thenReturn(Optional.empty());

        when(outgoingTransferRepository.save(any(OutgoingTransfer.class))).thenAnswer(
            i -> i.getArguments()[0]
        );

        CreateOutgoingTransferResult resultTransfer = createOutgoingTransfer.execute(command);

        // then
        assertThat(resultTransfer)
            .isNotNull()
            .extracting(
                CreateOutgoingTransferResult::id,
                CreateOutgoingTransferResult::toAccountNumber,
                CreateOutgoingTransferResult::amount,
                CreateOutgoingTransferResult::status,
                CreateOutgoingTransferResult::type,
                CreateOutgoingTransferResult::description,
                CreateOutgoingTransferResult::createdAt
            )
            .containsExactly(
                resultTransfer.id(),
                command.toAccountNumber(),
                command.amount(),
                OutgoingTransferStatus.PENDING,
                OutgoingTransferType.EXTERNAL,
                command.description(),
                resultTransfer.createdAt()
            );

        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));
    }

}