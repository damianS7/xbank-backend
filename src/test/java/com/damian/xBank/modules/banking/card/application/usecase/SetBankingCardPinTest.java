package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.usecase.set.pin.SetBankingCardPin;
import com.damian.xBank.modules.banking.card.application.usecase.set.pin.SetBankingCardPinCommand;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotOwnerException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SetBankingCardPinTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private SetBankingCardPin setBankingCardPin;

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

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US9900001111112233334444")
            .build();

        bankingCard = BankingCard
            .create(bankingAccount)
            .setId(11L)
            .setCardNumber("1234123412341234")
            .setCardCvv("123")
            .setCardPin("1234");
    }

    @Test
    @DisplayName("should return card with updated pin")
    void setPin_WhenValidRequest_ReturnsCardUpdated() {
        // given
        setUpContext(customer);

        SetBankingCardPinCommand command = new SetBankingCardPinCommand(
            bankingCard.getId(),
            "7777",
            RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        setBankingCardPin.execute(command);

        // then
        assertThat(bankingCard).isNotNull();
        assertThat(bankingCard.getCardPin()).isEqualTo(command.pin());
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("should throw exception when card not found")
    void setPin_WhenCardNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        SetBankingCardPinCommand command = new SetBankingCardPinCommand(
            1L,
            "7777",
            RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = Assert.assertThrows(
            BankingCardNotFoundException.class,
            () -> setBankingCardPin.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_FOUND);
    }

    @Test
    @DisplayName("should throw exception when customer not owner of the card")
    void setPin_WhenNotOwnerCard_ThrowsException() {
        // given
        User customerNotOwner = UserTestBuilder.aCustomer()
            .withId(99L)
            .withEmail("customerNotOwner@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        setUpContext(customerNotOwner);

        SetBankingCardPinCommand command = new SetBankingCardPinCommand(
            bankingCard.getId(),
            "7777",
            RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        BankingCardNotOwnerException exception = Assert.assertThrows(
            BankingCardNotOwnerException.class,
            () -> setBankingCardPin.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_CARD_NOT_OWNER);
    }

    @Test
    @DisplayName("should throw exception when password is invalid")
    void setPin_WhenPasswordIsInvalid_ThrowsException() {
        // given
        setUpContext(customer);

        SetBankingCardPinCommand command = new SetBankingCardPinCommand(
            bankingCard.getId(),
            "7777",
            "BAD_PASSWORD"
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(bankingCard));

        UserInvalidPasswordConfirmationException exception = Assert.assertThrows(
            UserInvalidPasswordConfirmationException.class,
            () -> setBankingCardPin.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_INVALID_PASSWORD);
    }
}