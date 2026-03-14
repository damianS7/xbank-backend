package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPaymentCommand;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardTestBuilder;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionTestBuilder;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
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
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US9900001111112233334444")
            .build();

        bankingCard = BankingCardTestBuilder.builder()
            .withId(11L)
            .withOwnerAccount(bankingAccount)
            .withCardNumber("1234123412341234")
            .withStatus(BankingCardStatus.ACTIVE)
            .withCVV("123")
            .withPIN("1234")
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
            .withStatus(BankingTransactionStatus.PENDING)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withDescription("AMAZON.COM")
            .build();

        CaptureCardPaymentCommand command = new CaptureCardPaymentCommand(
            transaction.getId()
        );

        when(bankingTransactionRepository.findById(anyLong()))
            .thenReturn(Optional.of(transaction));

        // then
        captureCardPayment.execute(command);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}