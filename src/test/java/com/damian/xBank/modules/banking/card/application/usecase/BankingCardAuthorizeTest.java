package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardAuthorizeRequest;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class BankingCardAuthorizeTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    @InjectMocks
    private BankingCardAuthorize cardAuthorize;

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
    @DisplayName("should authorize")
    void authorize_WhenValidRequest() {
        // given
        BankingCardAuthorizeRequest request = new BankingCardAuthorizeRequest(
                "Amazon.com",
                bankingCard.getCardNumber(),
                12,
                2025,
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                BigDecimal.valueOf(100)
        );

        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        givenBankingTransaction.setAmount(request.amount());
        givenBankingTransaction.setDescription(request.merchantName());

        when(bankingCardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(bankingCard));

        when(bankingTransactionPersistenceService.record(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransaction);

        // then
        cardAuthorize.execute(request);

        // then
    }

    // TODO add more tests
    //
    //    @Test
    //    @DisplayName("should throw exception when card not found")
    //    void spend_WhenCardNotFound_ThrowsException() {
    //        // given
    //        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
    //                BigDecimal.valueOf(100),
    //                "1234",
    //                "Amazon.com"
    //        );
    //
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());
    //
    //        // then
    //        BankingCardNotFoundException exception = assertThrows(
    //                BankingCardNotFoundException.class,
    //                () -> cardAuthorize.execute(
    //                        1L,
    //                        spendRequest
    //                )
    //        );
    //
    //        // then
    //        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    //    }
    //
    //    @Test
    //    @DisplayName("should throw exception when card is not active")
    //    void spend_WhenCardNotActive_ThrowsException() {
    //        // given
    //
    //        setUpContext(customer);
    //
    //        bankingCard.setStatus(BankingCardStatus.DISABLED);
    //
    //        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
    //                bankingAccount.getBalance(),
    //                bankingCard.getCardPin(),
    //                "Amazon.com"
    //        );
    //
    //        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
    //        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
    //        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        givenBankingTransaction.setDescription("Amazon.com");
    //
    //
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
    //
    //        // then
    //        BankingCardDisabledException exception = assertThrows(
    //                BankingCardDisabledException.class,
    //                () -> cardAuthorize.execute(
    //                        bankingCard.getId(),
    //                        spendRequest
    //                )
    //        );
    //
    //        // then
    //        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_DISABLED);
    //    }
    //
    //    @Test
    //    @DisplayName("should throw exception when card is locked")
    //    void spend_WhenCardLocked_ThrowsException() {
    //        // given
    //        setUpContext(customer);
    //        bankingCard.setStatus(BankingCardStatus.ACTIVE);
    //        bankingCard.setStatus(BankingCardStatus.LOCKED);
    //
    //        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
    //                bankingAccount.getBalance(),
    //                bankingCard.getCardPin(),
    //                "Amazon.com"
    //        );
    //
    //        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
    //        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
    //        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        givenBankingTransaction.setDescription("Amazon.com");
    //
    //
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
    //
    //        // then
    //        BankingCardLockedException exception = assertThrows(
    //                BankingCardLockedException.class,
    //                () -> cardAuthorize.execute(
    //                        bankingCard.getId(),
    //                        spendRequest
    //                )
    //        );
    //
    //        // then
    //        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_LOCKED);
    //    }
    //
    //    @Test
    //    @DisplayName("should throw exception when insufficient funds")
    //    void spend_WhenInsufficientFunds_ThrowsException() {
    //        // given
    //        setUpContext(customer);
    //
    //        bankingAccount.setBalance(BigDecimal.valueOf(0));
    //
    //        BankingCardSpendRequest spendRequest = new BankingCardSpendRequest(
    //                BigDecimal.valueOf(1000),
    //                bankingCard.getCardPin(),
    //                "Amazon.com"
    //        );
    //
    //        BankingTransaction givenBankingTransaction = new BankingTransaction(bankingAccount);
    //        givenBankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
    //        givenBankingTransaction.setAmount(BigDecimal.valueOf(100));
    //        givenBankingTransaction.setDescription("Amazon.com");
    //
    //
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
    //
    //        // then
    //        BankingCardInsufficientFundsException exception = assertThrows(
    //                BankingCardInsufficientFundsException.class,
    //                () -> cardAuthorize.execute(
    //                        bankingCard.getId(),
    //                        spendRequest
    //                )
    //        );
    //
    //        // then
    //        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS);
    //    }
}