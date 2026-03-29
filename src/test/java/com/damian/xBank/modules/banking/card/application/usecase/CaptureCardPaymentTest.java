package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPaymentCommand;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CaptureCardPaymentTest extends AbstractServiceTest {
    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private CaptureCardPayment captureCardPayment;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .withId(11L)
            .build();
    }

    @Test
    @DisplayName("should capture payment when payment is authorized")
    void capturePayment_WhenAuthorized_ReturnsTransactionCaptured() {
        // given
        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withId(1L)
            .withAccount(bankingAccount)
            .withCard(bankingCard)
            .withAmount(bankingAccount.getBalance())
            .withAuthorizationId("1233/1234")
            .withStatus(BankingTransactionStatus.PENDING)
            .withPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withDescription("AMAZON.COM")
            .build();

        CaptureCardPaymentCommand command = new CaptureCardPaymentCommand(
            transaction.getAuthorizationId()
        );

        when(bankingTransactionRepository.findByAuthorizationId(anyString()))
            .thenReturn(Optional.of(transaction));

        // then
        captureCardPayment.execute(command);

        assertThat(transaction)
            .isNotNull()
            .extracting(
                BankingTransaction::getPaymentStatus
            ).isEqualTo(BankingTransactionPaymentStatus.CAPTURED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}