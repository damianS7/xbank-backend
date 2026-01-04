package com.damian.xBank.modules.notification.infrastructure.web.controller;

import com.damian.xBank.modules.notification.application.dto.request.NotificationDeleteRequest;
import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
import com.damian.xBank.modules.notification.application.usecase.NotificationSinkGet;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.user.domain.model.UserAccountStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.security.UserPrincipal;
import com.damian.xBank.shared.utils.JwtUtil;
import com.damian.xBank.shared.utils.UserTestBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationControllerTest extends AbstractControllerTest {

    @Autowired
    private JwtUtil jwtUtil;

    @SpyBean
    private NotificationSinkGet notificationSinkGet;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserAccountRole.CUSTOMER)
                .withStatus(UserAccountStatus.VERIFIED)
                .withPassword(passwordEncoder.encode(RAW_PASSWORD))
                .build();

        userAccountRepository.save(customer);
    }

    @Test
    @DisplayName("GET /notifications with valid request should get notifications for authenticated user")
    void getNotifications_ValidRequest_Returns200OK() throws Exception {
        // given
        Notification notification = Notification
                .create(customer)
                .setMessage("Alice has follow you.")
                .setType(NotificationType.INFO)
                .setMetadata(
                        Map.of(
                                "userName", "alice"
                        )
                );

        notificationRepository.save(notification);

        // when
        login(customer);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .get("/api/v1/notifications")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andDo(print())
                                  .andExpect(status().is(HttpStatus.OK.value()))
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        NotificationDto[] notificationsDto = objectMapper.treeToValue(contentNode, NotificationDto[].class);

        // then
        assertThat(notificationsDto.length).isEqualTo(1L);
    }

    @Test
    @DisplayName("DELETE /notifications should delete notifications for authenticated user")
    void deleteNotifications_ValidRequest_Returns204NoContent() throws Exception {
        // given
        Notification notification = Notification
                .create(customer)
                .setMessage("Alice has follow you.")
                .setType(NotificationType.INFO)
                .setMetadata(
                        Map.of(
                                "userName", "alice"
                        )
                );

        notificationRepository.save(notification);

        NotificationDeleteRequest notificationDeleteRequest = new NotificationDeleteRequest(
                List.of(notification.getId())
        );

        // when
        login(customer);
        mockMvc.perform(MockMvcRequestBuilders
                       .delete("/api/v1/notifications")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(notificationDeleteRequest))
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    @DisplayName("GET /notifications/stream returns real-time notifications for authenticated user")
    void getNotificationsStream_ValidRequest_ReturnsSseStreamAnd200OK() throws Exception {
        // given
        login(customer);

        NotificationEvent notificationEvent = new NotificationEvent(
                customer.getId(),
                NotificationType.INFO,
                Map.of("postId", 123),
                "2025-09-10T00:00:00"
        );

        UserPrincipal user = new UserPrincipal(customer);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        when(notificationSinkGet.execute())
                .thenReturn(Flux.just(notificationEvent));

        MvcResult result = mockMvc.perform(get("/api/v1/notifications/stream")
                                          .accept(MediaType.TEXT_EVENT_STREAM)
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andExpect(status().is(HttpStatus.OK.value()))
                                  .andReturn();

        String rawResponse = result.getResponse().getContentAsString();
        // Cada evento SSE va en una línea, empieza con "data:"
        String json = rawResponse.replaceFirst("data:", "").trim();

        NotificationDto notificationDto = objectMapper.readValue(
                json,
                NotificationDto.class
        );

        assertThat(notificationDto)
                .isNotNull()
                .extracting(
                        NotificationDto::type,
                        NotificationDto::metadata,
                        NotificationDto::createdAt
                ).containsExactly(
                        notificationEvent.type(),
                        notificationEvent.metadata(),
                        notificationEvent.createdAt()
                );
    }
}
