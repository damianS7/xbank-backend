package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCard;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardCommand;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardTestBuilder;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.service.BankingCardDomainService;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class RequestCardRequestTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardDomainService bankingCardDomainService;

    @InjectMocks
    private RequestCard requestCard;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("Should returns a BankingCard linked to the account when request is valid")
    void cardRequest_WhenValidRequest_ReturnsBankingCard() {
        // given
        setUpContext(customer);
        // TODO user Factory
        BankingCard givenBankingCard = BankingCardTestBuilder.builder()
            .withId(11L)
            .withOwnerAccount(bankingAccount)
            .withCardNumber("1234567890123456")
            .withStatus(BankingCardStatus.ACTIVE)
            .withCVV("123")
            .withPIN("1234")
            .build();

        RequestCardCommand command = new RequestCardCommand(
            bankingAccount.getId(),
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingCardDomainService
            .createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
            .thenReturn(givenBankingCard);

        RequestCardResult result = requestCard.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.cardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(result.cardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should throws exception when banking account not found")
    void cardRequest_WhenAccountNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        RequestCardCommand command = new RequestCardCommand(
            bankingAccount.getId(),
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
            BankingAccountNotFoundException.class,
            () -> requestCard.execute(command)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should throws exception when authenticated customer is not the owner of the account")
    void cardRequest_WhenAccountNotOwnedByCustomer_ThrowsException() {
        // given
        User customerB = UserTestFactory.aCustomer()
            .withId(2L)
            .withEmail("customerB@demo.com")
            .build();

        setUpContext(customerB);

        RequestCardCommand command = new RequestCardCommand(
            99L,
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountNotOwnerException exception = assertThrows(
            BankingAccountNotOwnerException.class,
            () -> requestCard.execute(command)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER, exception.getMessage());
    }

    @Test
    @DisplayName("Should returns a BankingCard when account is not yours but you are admin")
    void cardRequest_WhenAccountNotOwnedByCustomerButItIsAdmin_ReturnsBankingCard() {
        // given
        User admin = UserTestFactory.anAdmin()
            .withId(5L)
            .build();

        setUpContext(admin);

        BankingCard givenBankingCard = BankingCardTestBuilder.builder()
            .withId(11L)
            .withOwnerAccount(bankingAccount)
            .withCardNumber("1234567890123456")
            .withStatus(BankingCardStatus.ACTIVE)
            .withCVV("123")
            .withPIN("1234")
            .build();

        RequestCardCommand command = new RequestCardCommand(
            bankingAccount.getId(),
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingCardDomainService.createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
            .thenReturn(givenBankingCard);

        RequestCardResult result = requestCard.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.cardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(result.cardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should throws exception when cards per account reached limit")
    void cardRequest_WhenCardLimitPerAccountReached_ThrowsException() {
        // given
        setUpContext(customer);

        Set<BankingCard> bankingCards = new HashSet<>();

        for (int i = 0; i <= BankingAccount.MAX_CARDS_PER_ACCOUNT; i++) {
            BankingCard card = BankingCardTestBuilder.builder()
                .withId((long) i)
                .withOwnerAccount(bankingAccount)
                .withCardNumber("123412341234123" + i)
                .withStatus(BankingCardStatus.ACTIVE)
                .withCVV("123")
                .withPIN("1234")
                .build();
            bankingCards.add(card);
        }

        BankingAccount bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(2L)
            .withBalance(BigDecimal.valueOf(1000))
            .withCards(bankingCards)
            .build();

        RequestCardCommand command = new RequestCardCommand(
            bankingAccount.getId(),
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong()))
            .thenReturn(Optional.of(bankingAccount));
        when(bankingCardDomainService.createBankingCard(any(), any()))
            .thenAnswer(invocation -> {
                BankingAccount account = invocation.getArgument(0);
                BankingCardType type = invocation.getArgument(1);

                return account.issueCard(
                    type,
                    "1234567812345678",
                    "123",
                    "1234"
                );
            });

        BankingAccountCardsLimitException exception = assertThrows(
            BankingAccountCardsLimitException.class,
            () -> requestCard.execute(command)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_CARD_LIMIT, exception.getMessage());
    }
}