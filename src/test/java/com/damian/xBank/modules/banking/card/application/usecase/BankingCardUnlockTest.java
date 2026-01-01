package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUnlockRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotOwnerException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BankingCardUnlockTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardUnlock bankingCardUnlock;

    private Customer customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

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
    @DisplayName("should return card unlocked")
    void unlockCard_WhenValid_ReturnsUnlockedCard() {
        // given
        bankingCard.setStatus(BankingCardStatus.ACTIVE);
        bankingCard.setStatus(BankingCardStatus.LOCKED);
        setUpContext(customer);

        BankingCardUnlockRequest givenRequest = new BankingCardUnlockRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingCardUnlock.execute(
                bankingCard.getId(),
                givenRequest
        );

        // then
        assertThat(bankingCard).isNotNull();
        assertThat(bankingCard.getStatus()).isEqualTo(BankingCardStatus.ACTIVE);
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void unlockCard_WhenCardNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        BankingCardUnlockRequest request = new BankingCardUnlockRequest(RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = Assert.assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardUnlock.execute(1L, request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when card not owner")
    void unlockCard_WhenNotOwnerCard_ThrowsException() {
        // given
        Customer customerNotOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerNotOwner@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customerNotOwner);

        BankingCardUnlockRequest request = new BankingCardUnlockRequest(RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        BankingCardNotOwnerException exception = Assert.assertThrows(
                BankingCardNotOwnerException.class,
                () -> bankingCardUnlock.execute(bankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_OWNER);
    }

    @Test
    @DisplayName("should throw exception when password is invalid")
    void unlockCard_WhenPasswordIsInvalid_ThrowsException() {
        // given
        setUpContext(customer);

        BankingCardUnlockRequest request = new BankingCardUnlockRequest("BAD_PASSWORD");

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        UserAccountInvalidPasswordConfirmationException exception = Assert.assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingCardUnlock.execute(bankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD);
    }
}