package com.example.journalApp.repository;

import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.bson.Document;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Test
    void getUserForSentimentAnalysis_shouldReturnUsersWithNameYogi() {

        // Arrange
        UserEntity user = new UserEntity();
        user.setUserName("Yogi");

        List<UserEntity> expectedUsers = List.of(user);

        when(mongoTemplate.find(
                any(Query.class),
                eq(UserEntity.class)
        )).thenReturn(expectedUsers);

        // Act
        List<UserEntity> actualUsers =
                userRepository.getUserForSentimentAnalysisForUserName();

        // Assert
        assertEquals(expectedUsers, actualUsers);

        // Capture the Query passed to MongoTemplate
        ArgumentCaptor<Query> queryCaptor =
                ArgumentCaptor.forClass(Query.class);

        verify(mongoTemplate).find(
                queryCaptor.capture(),
                eq(UserEntity.class)
        );

        Query capturedQuery = queryCaptor.getValue();
        // Verify that the query searches for name = "Yogi"
        assertEquals("Yogi", capturedQuery.getQueryObject().getString("name"));
    }

    @Test
    void getUserForSentimentAnalysis_shouldReturnUsersWithValidEmailAndSentimentAnalysisEnabled() {

        // Arrange
        UserEntity user = new UserEntity();
        user.setUserName("Ram");
        user.setEmail("ram_test@gmail.com");
        user.setSentimentalAnalysisEnabled(true);

        List<UserEntity> expectedUsers = List.of(user);

        when(mongoTemplate.find(
                any(Query.class),
                eq(UserEntity.class)
        )).thenReturn(expectedUsers);

        // Act
        List<UserEntity> actualUsers =
                userRepository.getUserForSentimentAnalysis();

        // Assert - returned users
        assertEquals(expectedUsers, actualUsers);

        // Capture the Query passed to MongoTemplate
        ArgumentCaptor<Query> queryCaptor =
                ArgumentCaptor.forClass(Query.class);

        verify(mongoTemplate).find(
                queryCaptor.capture(),
                eq(UserEntity.class)
        );

        Query capturedQuery = queryCaptor.getValue();

        Document queryObject = capturedQuery.getQueryObject();

        // Verify sentimentalAnalysisEnabled = true
        assertEquals(
                true,
                queryObject.getBoolean("sentimentalAnalysisEnabled")
        );

        // Verify email regex
        Pattern emailPattern =
                (Pattern) queryObject.get("email");

        assertEquals(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$",
                emailPattern.pattern()
        );

        // Verify regex is case-insensitive
        assertEquals(
                Pattern.CASE_INSENSITIVE,
                emailPattern.flags()
        );
    }
}