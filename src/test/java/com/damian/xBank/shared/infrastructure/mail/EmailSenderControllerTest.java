package com.damian.xBank.shared.infrastructure.mail;

import com.damian.xBank.infrastructure.mail.EmailSenderService;
import com.damian.xBank.shared.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EmailSenderControllerTest extends AbstractControllerTest {

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