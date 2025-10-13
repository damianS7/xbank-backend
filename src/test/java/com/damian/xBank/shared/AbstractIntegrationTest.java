package com.damian.xBank.shared;


import com.damian.xBank.modules.auth.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.dto.AuthenticationResponse;
import com.damian.xBank.modules.notification.repository.NotificationRepository;
import com.damian.xBank.modules.setting.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.UserAccountTokenRepository;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.User;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {
    @Container
    @ServiceConnection
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withReuse(true);

    protected final String RAW_PASSWORD = "123456";

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected UserAccountTokenRepository userAccountTokenRepository;

    @Autowired
    protected UserAccountRepository userAccountRepository;

    @Autowired
    protected SettingRepository settingRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    protected String token;

    @AfterAll
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        notificationRepository.deleteAll();
        settingRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    protected void login(UserAccount userAccount) throws Exception {
        // given
        final HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", userAccount.getEmail());
        claims.put("role", userAccount.getRole());

        token = jwtUtil.generateToken(claims, userAccount.getEmail());
    }

    protected void login(Customer customer) throws Exception {
        login(customer.getAccount());
    }

    protected void login(User user) throws Exception {
        login(user.getAccount());
    }

    protected void loginWithPost(UserAccount user) throws Exception {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                user.getEmail(), "123456"
        );

        String jsonRequest = objectMapper.writeValueAsString(authenticationRequest);

        // when
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonRequest))
                                  .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        token = response.token();

    }
}