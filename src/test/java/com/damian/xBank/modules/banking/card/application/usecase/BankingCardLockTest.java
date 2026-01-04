package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardLockRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotOwnerException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BankingCardLockTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardLock bankingCardLock;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@demo.com")
                                  .withPassword(RAW_PASSWORD)
                                  .build();

        bankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        bankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("should return a locked BankingCard")
    void lock_WhenValidRequest_ReturnsBankingCardLocked() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);
        setUpContext(customer);

        BankingCardLockRequest givenRequest = new BankingCardLockRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingCardLock.execute(bankingCard.getId(), givenRequest);

        // then
        assertThat(bankingCard).isNotNull();
        assertThat(bankingCard.getStatus()).isEqualTo(BankingCardStatus.LOCKED);
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void lock_WhenCardNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        BankingCardLockRequest request = new BankingCardLockRequest(RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = Assert.assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardLock.execute(1L, request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when customer not the owner of the card")
    void lock_WhenNotOwner_ThrowsException() {
        // given
        User customerNotOwner = UserTestBuilder.aCustomer()
                                               .withId(2L)
                                               .withEmail("customerNotOwner@demo.com")
                                               .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                               .build();

        setUpContext(customerNotOwner);

        BankingCardLockRequest request = new BankingCardLockRequest(RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        BankingCardNotOwnerException exception = Assert.assertThrows(
                BankingCardNotOwnerException.class,
                () -> bankingCardLock.execute(bankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_OWNER);
    }

    @Test
    @DisplayName("should throw exception when password is invalid")
    void lock_WhenPasswordIsInvalid_ThrowsException() {
        // given
        setUpContext(customer);

        BankingCardLockRequest request = new BankingCardLockRequest("BAD_PASSWORD");

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        UserInvalidPasswordConfirmationException exception = Assert.assertThrows(
                UserInvalidPasswordConfirmationException.class,
                () -> bankingCardLock.execute(bankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD);
    }
}