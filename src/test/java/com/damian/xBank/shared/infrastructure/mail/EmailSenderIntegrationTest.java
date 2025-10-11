package com.damian.xBank.shared.infrastructure.mail;

import com.damian.xBank.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EmailSenderIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    void shouldSendEmail() {
        emailSenderService.send(
                "customer@demo.com",
                "hello",
                "hello world"
        );
    }
}