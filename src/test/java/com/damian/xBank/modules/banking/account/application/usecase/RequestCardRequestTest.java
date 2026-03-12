package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCard;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardCommand;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.service.BankingCardDomainService;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
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
            .withAccountNumber("US1200001111112233335555")
            .build();

    }

    @Test
    @DisplayName("Should returns a BankingCard linked to the account when request is valid")
    void cardRequest_WhenValidRequest_ReturnsBankingCard() {
        // given
        setUpContext(customer);

        BankingCard givenBankingCard = BankingCard
            .create(bankingAccount)
            .setId(11L)
            .setCardNumber("1234567890123456");

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
        User customerB = UserTestBuilder.aCustomer()
            .withId(2L)
            .withEmail("customerB@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
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
        User admin = UserTestBuilder.aCustomer()
            .withId(5L)
            .withRole(UserRole.ADMIN)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        setUpContext(admin);

        BankingCard givenBankingCard = BankingCard
            .create(bankingAccount)
            .setId(11L)
            .setCardNumber("1234567890123456");

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

        for (int i = 0; i < bankingAccount.getCardLimit(); i++) {
            bankingAccount.addBankingCard(
                BankingCard.create(bankingAccount).setStatus(BankingCardStatus.ACTIVE)
            );
        }

        RequestCardCommand command = new RequestCardCommand(
            bankingAccount.getId(),
            BankingCardType.CREDIT
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountCardsLimitException exception = assertThrows(
            BankingAccountCardsLimitException.class,
            () -> requestCard.execute(command)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_CARD_LIMIT, exception.getMessage());
    }
}