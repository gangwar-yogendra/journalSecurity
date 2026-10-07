package com.example.journalApp.scheduler;

import com.example.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_REAL_EMAIL_TEST", matches = "true")
class RealEmailIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Value("${test.email.recipient:gangwar.yogendra@gmail.com}")
    private String recipientEmail;

    @Test
    void shouldSendRealEmailThroughSpringBootContext() {
        assertDoesNotThrow(() ->
                emailService.sendEmail(
                        recipientEmail,
                        "Journal Security test email",
                        "This is a real integration test email from Journal Security."
                )
        );
    }
}
