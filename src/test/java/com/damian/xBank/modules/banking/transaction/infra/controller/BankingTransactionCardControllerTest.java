package com.damian.xBank.modules.banking.transaction.infra.controller;

//import com.damian.xBank.modules.auth.http.AuthenticationRequest;
//import com.damian.xBank.modules.auth.http.AuthenticationResponse;
//import com.damian.xBank.modules.banking.account.*;
//import com.damian.xBank.modules.banking.card.BankingCard;
//import com.damian.xBank.modules.banking.card.BankingCardStatus;
//import com.damian.xBank.modules.banking.card.BankingCardType;
//import com.damian.xBank.modules.banking.transactions.BankingTransactionType;
//import com.damian.xBank.modules.banking.transactions.http.BankingCardTransactionRequest;
//import com.damian.xBank.modules.customer.CustomerRole;
//import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
//import com.damian.xBank.shared.domain.Customer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles("test")
//@SpringBootTest
//@AutoConfigureMockMvc
public class BankingTransactionCardControllerTest {
    //    @Test
    //    @DisplayName("Should fetch transactions (pageable) for banking card")
    //    void shouldFetchBankingCardTransactions() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //
    //        BankingAccount bankingAccount = new BankingAccount(customerA);
    //        bankingAccount.setAccountNumber("ES1234567890123456789012");
    //        bankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccount.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccount.setBalance(BigDecimal.valueOf(1000));
    //
    //        BankingCard bankingCard = new BankingCard();
    //        bankingCard.setCardType(BankingCardType.CREDIT);
    //        bankingCard.setCardNumber("1234567890123456");
    //        bankingCard.setCardStatus(BankingCardStatus.ENABLED);
    //        bankingCard.setAssociatedBankingAccount(bankingAccount);
    //
    //        bankingAccount.addBankingCard(bankingCard);
    //        bankingAccountRepository.save(bankingAccount);
    //
    //        // when
    //        // then
    //        mockMvc
    //                .perform(
    //                        get("/api/v1/customers/me/banking/cards/{id}/transactions", bankingCard.getId())
    //                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
    //                .andDo(print())
    //                .andExpect(status().is(200))
    //                .andExpect(jsonPath("$.content").isArray())
    //                .andExpect(jsonPath("$.content.length()").value(0)) // o el número que esperás
    //                .andExpect(jsonPath("$.totalPages").value(0));
    //    }
}