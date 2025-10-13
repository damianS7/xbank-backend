package com.damian.xBank.modules.auth;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountUpdateRequest;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.CustomerGender;
import com.damian.xBank.shared.AbstractIntegrationTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    private Customer customer;

    @BeforeAll
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setFirstName("David")
                           .setLastName("Brow")
                           .setBirthdate(LocalDate.now())
                           .setPhotoPath("avatar.jpg")
                           .setPhone("123 123 123")
                           .setPostalCode("01003")
                           .setAddress("Fake ave")
                           .setCountry("US")
                           .setGender(CustomerGender.MALE);
        customer.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should have access when token is valid")
    void shouldHaveAccessWhenTokenIsValid() throws Exception {
        // given
        final String givenToken = jwtUtil.generateToken(
                customer.getAccount().getEmail(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when not authenticated")
    void shouldNotHaveAccessWhenNotAuthenticated() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers"))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token has expired")
    void shouldNotHaveAccessWhenTokenHasExpired() throws Exception {
        // given
        final String expiredToken = jwtUtil.generateToken(
                customer.getEmail(),
                new Date(System.currentTimeMillis() - 1000 * 60 * 60)
        );

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserAccountUpdateRequest request = new UserAccountUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(Exceptions.JWT.TOKEN.EXPIRED))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token is invalid")
    void shouldNotHaveAccessWhenTokenIsInvalid() throws Exception {
        // given
        final String invalidToken = "bad-token";

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserAccountUpdateRequest request = new UserAccountUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(Exceptions.JWT.TOKEN.INVALID))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token email not exists")
    void shouldNotHaveAccessWhenTokenEmailNotExists() throws Exception {
        // given
        final String token = jwtUtil.generateToken(
                "fake-email@demo.com",
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
