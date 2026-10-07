package com.example.journalApp.scheduler;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.enums.Sentiment;
import com.example.journalApp.repository.UserRepositoryImpl;
import com.example.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserSchedulerSpringBootTest {

    @Autowired
    private UserScheduler userScheduler;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private UserRepositoryImpl userRepository;

    @Test
    void fetchUserAndSendSentimentalAnalysisEmail_shouldSendEmailForRecentSentiments() {
        UserEntity user = new UserEntity();
        user.setUserName("yogendra");
        user.setEmail("gangwar.yogendra@gmail.com");

        JournalEntry happy1 = new JournalEntry();
        happy1.setDate(new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000));
        happy1.setSentiment(Sentiment.HAPPY);

        JournalEntry happy2 = new JournalEntry();
        happy2.setDate(new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000));
        happy2.setSentiment(Sentiment.HAPPY);

        JournalEntry sad = new JournalEntry();
        sad.setDate(new Date(System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1000));
        sad.setSentiment(Sentiment.SAD);

        user.setJournalEntries(List.of(happy1, happy2, sad));

        when(userRepository.getUserForSentimentAnalysis()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentalAnalysisEmail();

        verify(emailService).sendEmail(
                "gangwar.yogendra@gmail.com",
                "Your Sentiment Analysis Report for Last 7 Days",
                "Hello yogendra,\n\n" +
                        "Based on your recent journal entries from the last 7 days, your overall sentiment is: HAPPY.\n\n" +
                        "Keep journaling to track your thoughts and feelings!\n\n" +
                        "Best regards,\n" +
                        "Your Journal App Team"
        );
    }
}
