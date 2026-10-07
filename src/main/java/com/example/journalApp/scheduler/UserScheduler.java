package com.example.journalApp.scheduler;

import com.example.journalApp.cache.AppCache;
import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.enums.Sentiment;
import com.example.journalApp.repository.UserRepositoryImpl;
import com.example.journalApp.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    // Fetch user for a cron job and send mail to those users who have not written their journal for the da
    //@Scheduled(cron = "0 0 9 * * SUN") // Adjust the cron expression as needed for Every Sunday at 9 AM
    //@Scheduled(cron = "0 * * ? * *") // Adjust the cron expression as needed for testing (every minute)
    public void fetchUserAndSendSentimentalAnalysisEmail()
    {
        List<UserEntity> users = userRepository.getUserForSentimentAnalysis();

        if (users == null || users.isEmpty()) {
            log.info("No users found for sentiment analysis.");
            return;
        }

        Date sevenDaysAgo = Date.from( Instant.now().minus(7, ChronoUnit.DAYS) );

        for(UserEntity user: users)
        {
            if(user == null)
            {
                continue;
            }

            List<JournalEntry> journalEntries = user.getJournalEntries();

            if (journalEntries == null || journalEntries.isEmpty())
            {
                log.info( "No journal entries found for user: {}", user.getUserName() );
                continue;
            }

            List<Sentiment> sentiments =
                    journalEntries.stream()
                            .filter(Objects::nonNull)
                            .filter(entry -> entry.getDate() != null)
                            .filter(entry -> entry.getDate().after(sevenDaysAgo)).map(x->x.getSentiment())
                            .filter(Objects::nonNull)
                            //.filter(content -> !content.isBlank())
                            .collect(Collectors.toList());

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();


            for(Sentiment sentiment: sentiments)
            {
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;

            for(Map.Entry<Sentiment, Integer> entry: sentimentCounts.entrySet())
            {
                if(entry.getValue() > maxCount)
                {
                    mostFrequentSentiment = entry.getKey();
                    maxCount = entry.getValue();
                }
            }

            if(mostFrequentSentiment != null)
            {
                emailService.sendEmail(user.getEmail(), "Your Sentiment Analysis Report for Last 7 Days", "Hello " + user.getUserName() + ",\n\n" +
                        "Based on your recent journal entries from the last 7 days, your overall sentiment is: " + mostFrequentSentiment.name() + ".\n\n" +
                        "Keep journaling to track your thoughts and feelings!\n\n" +
                        "Best regards,\n" +
                        "Your Journal App Team");
            }
        }
    }


    @Scheduled(cron = "0 0 9 * * SUN") // Adjust the cron expression as needed for Every Sunday at 9 AM
    // Make a scheduler so every 10 min it will clear the cache as we were doing on start the application using AppCache.init()
    // @Scheduled(cron = "0 0/10 * ? * *") // Adjust the cron expression as needed for testing (every 10 minutes)
    public void clearAppCache()
    {
        appCache.init();
    }
}
