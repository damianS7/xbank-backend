package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BankingCardServiceTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private Faker faker;

    @InjectMocks
    private BankingCardService bankingCardService;

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create()
                .setOwner(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setId(11L)
                .setBankingAccount(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("Should get customer cards")
    void shouldGetCustomerCards() {
        // given

        // when
        when(bankingCardRepository.findCardsByCustomerId(anyLong())).thenReturn(
                Set.of(customerBankingCard)
        );

        Set<BankingCard> customerCards = bankingCardService.getCustomerBankingCards(
                customer.getId()
        );

        // then
        assertThat(customerCards)
                .isNotNull()
                .size()
                .isEqualTo(1);

        verify(bankingCardRepository, times(1)).findCardsByCustomerId(anyLong());
    }

    @Test
    @DisplayName("Should create a BankingCard with generated data and persist it")
    void shouldCreateBankingCard() {
        // given
        final Number numberMock = mock(Number.class);

        final String cardNumber = "1234123412341234";
        final String cardPIN = "1234";
        final String cardCVV = "000";

        // when
        when(faker.number()).thenReturn(numberMock);
        when(numberMock.digits(3)).thenReturn(cardCVV);
        when(numberMock.digits(4)).thenReturn(cardPIN);
        when(numberMock.digits(16)).thenReturn(cardNumber);
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingCard createdCard = bankingCardService.createBankingCard(
                customerBankingAccount,
                BankingCardType.DEBIT
        );

        // then
        assertThat(createdCard)
                .isNotNull()
                .extracting(
                        BankingCard::getBankingAccount,
                        BankingCard::getCardType
                )
                .containsExactly(
                        customerBankingAccount,
                        BankingCardType.DEBIT
                );

        assertThat(createdCard.getCardPin())
                .matches("\\d{4}") // 4 digits
                .isNotNull();

        assertThat(createdCard.getCardCvv())
                .matches("\\d{3}") // 4 digits
                .isNotNull();

        assertThat(createdCard.getCardNumber())
                .matches("\\d{16}") // 4 digits
                .isNotNull();

        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("Should generate a card number")
    void shouldGenerateCardNumber() {
        // given
        final Number numberMock = mock(Number.class);
        final String expectedCardNumber = "1234123412341234";

        when(faker.number()).thenReturn(numberMock);
        when(numberMock.digits(16)).thenReturn(expectedCardNumber);

        // when
        String generatedCardNumber = bankingCardService.generateCardNumber();

        // then
        assertThat(generatedCardNumber)
                .isNotNull()
                .isEqualTo(expectedCardNumber)
                .matches("\\d{16}");

        verify(numberMock).digits(16);
    }

    @Test
    @DisplayName("Should generate a card pin")
    void shouldGeneratePin() {
        // given
        final Number numberMock = mock(Number.class);
        final String expectedCardPin = "1234";

        when(faker.number()).thenReturn(numberMock);
        when(numberMock.digits(4)).thenReturn(expectedCardPin);

        // when
        String generatedCardPin = bankingCardService.generateCardPIN();

        // then
        assertThat(generatedCardPin)
                .isNotNull()
                .isEqualTo(expectedCardPin)
                .matches("\\d{4}");

        verify(numberMock).digits(4);
    }

    @Test
    @DisplayName("Should generate a card cvv")
    void shouldGenerateCvv() {
        // given
        final Number numberMock = mock(Number.class);
        final String expectedCardCvv = "123";

        when(faker.number()).thenReturn(numberMock);
        when(numberMock.digits(3)).thenReturn(expectedCardCvv);

        // when
        String generatedCardCvv = bankingCardService.generateCardCVV();

        // then
        assertThat(generatedCardCvv)
                .isNotNull()
                .isEqualTo(expectedCardCvv)
                .matches("\\d{3}");

        verify(numberMock).digits(3);
    }
}
