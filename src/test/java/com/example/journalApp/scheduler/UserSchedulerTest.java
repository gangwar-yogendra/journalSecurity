package com.example.journalApp.scheduler;

import com.example.journalApp.cache.AppCache;
import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.enums.Sentiment;
import com.example.journalApp.repository.UserRepositoryImpl;
import com.example.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private AppCache appCache;

    @InjectMocks
    private UserScheduler userScheduler;

    @Test
    void fetchUserAndSendSentimentalAnalysisEmail_shouldSendMailForMostFrequentRecentSentiment() {
        UserEntity user = new UserEntity();
        user.setUserName("yogendra");
        user.setEmail("test@example.com");

        JournalEntry recentHappy1 = new JournalEntry();
        recentHappy1.setTitle("Day 1");
        recentHappy1.setDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
        recentHappy1.setSentiment(Sentiment.HAPPY);

        JournalEntry recentHappy2 = new JournalEntry();
        recentHappy2.setTitle("Day 2");
        recentHappy2.setDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));
        recentHappy2.setSentiment(Sentiment.HAPPY);

        JournalEntry recentSad = new JournalEntry();
        recentSad.setTitle("Day 3");
        recentSad.setDate(Date.from(Instant.now().minus(3, ChronoUnit.DAYS)));
        recentSad.setSentiment(Sentiment.SAD);

        JournalEntry oldAngry = new JournalEntry();
        oldAngry.setTitle("Old entry");
        oldAngry.setDate(Date.from(Instant.now().minus(10, ChronoUnit.DAYS)));
        oldAngry.setSentiment(Sentiment.ANGRY);

        user.setJournalEntries(List.of(recentHappy1, recentHappy2, recentSad, oldAngry));

        when(userRepository.getUserForSentimentAnalysis()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentalAnalysisEmail();

        verify(emailService).sendEmail(
                "test@example.com",
                "Your Sentiment Analysis Report for Last 7 Days",
                "Hello yogendra,\n\n" +
                        "Based on your recent journal entries from the last 7 days, your overall sentiment is: HAPPY.\n\n" +
                        "Keep journaling to track your thoughts and feelings!\n\n" +
                        "Best regards,\n" +
                        "Your Journal App Team"
        );
        verify(appCache, never()).init();
    }

    @Test
    void clearAppCache_shouldReinitializeCache() {
        userScheduler.clearAppCache();

        verify(appCache).init();
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
