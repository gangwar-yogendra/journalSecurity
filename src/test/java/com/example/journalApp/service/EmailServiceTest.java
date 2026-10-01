package com.example.journalApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
//    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendEmail() {
        // Arrange
        String to = "test@gmail.com";
        String subject = "Test Subject";
        String body = "Hello, this is a test email. Test again";

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }
}