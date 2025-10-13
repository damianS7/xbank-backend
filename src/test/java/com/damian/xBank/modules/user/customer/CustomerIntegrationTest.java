package com.damian.xBank.modules.user.customer;

import com.damian.xBank.modules.user.customer.dto.CustomerWithAccountDto;
import com.damian.xBank.modules.user.customer.enums.CustomerGender;
import com.damian.xBank.shared.AbstractIntegrationTest;
import com.damian.xBank.shared.domain.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerIntegrationTest extends AbstractIntegrationTest {
    private Customer customer;

    @BeforeAll
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@test.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setNationalId("123456789Z")
                           .setFirstName("John")
                           .setLastName("Wick")
                           .setGender(CustomerGender.MALE)
                           .setBirthdate(LocalDate.of(1989, 1, 1))
                           .setCountry("USA")
                           .setAddress("fake ave")
                           .setPostalCode("050012")
                           .setPhotoPath("no photoPath");
        customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should get logged customer")
    void shouldGetCustomer() throws Exception {
        // given
        login(customer);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/customers")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        CustomerWithAccountDto customerWithProfileDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerWithAccountDto.class
        );

        // then
        assertThat(customerWithProfileDTO).isNotNull();
        assertThat(customerWithProfileDTO.email()).isEqualTo(customer.getAccount().getEmail());
    }

    // TODO shouldUpdateFields
}
