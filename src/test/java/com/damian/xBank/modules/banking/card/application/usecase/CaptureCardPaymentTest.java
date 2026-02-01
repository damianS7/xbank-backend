package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.CaptureCardPaymentRequest;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
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

        bankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        bankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("should capture payment when payment is authorized")
    void capturePayment_WhenAuthorized_ReturnsTransactionCaptured() {
        // given
        BankingTransaction transaction = new BankingTransaction(bankingAccount);
        transaction.setId(1L);
        transaction.setBankingCard(bankingCard);
        transaction.setType(BankingTransactionType.CARD_CHARGE);
        transaction.setAmount(bankingAccount.getBalance());
        transaction.setDescription("AMAZON.COM");

        CaptureCardPaymentRequest request = new CaptureCardPaymentRequest(
                transaction.getId()
        );

        when(bankingTransactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(transaction));

        // then
        captureCardPayment.execute(request);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(bankingAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}