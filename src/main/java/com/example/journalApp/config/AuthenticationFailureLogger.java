package com.example.journalApp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFailureLogger {

    // Fires for ANY failed authentication attempt (bad password, unknown username, etc.)
    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String reason = event.getException().getMessage();
        log.error("Authentication failed for username: {} - reason: {}", username, reason);
    }
}
