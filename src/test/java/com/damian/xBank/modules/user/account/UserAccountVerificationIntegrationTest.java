package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.token.UserAccountTokenType;
import com.damian.xBank.shared.AbstractIntegrationTest;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserAccountToken;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountVerificationIntegrationTest extends AbstractIntegrationTest {
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.create()
                                 .setEmail("user@demo.com")
                                 .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                 .setRole(UserAccountRole.ADMIN);

        userAccount.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(userAccount);
    }

    @AfterEach
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should verify account using token")
    void shouldVerifyAccountUsingToken() throws Exception {
        // given
        UserAccount unverifiedUser = UserAccount.create()
                                                .setEmail("non-verified-user@demo.com")
                                                .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                                .setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);
        userAccountRepository.save(unverifiedUser);

        UserAccountToken givenToken = UserAccountToken.create()
                                                      .setType(UserAccountTokenType.ACCOUNT_VERIFICATION)
                                                      .setAccount(unverifiedUser);
        userAccountTokenRepository.save(givenToken);

        UserAccountPasswordResetSetRequest request = new UserAccountPasswordResetSetRequest(
                "12345678$Xa"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/verification/{token}", givenToken.getToken())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

}
