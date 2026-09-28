package com.example.journalApp.repository;

import com.example.journalApp.entity.User;
import com.example.journalApp.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Test
    void getUserForSentimentAnalysis_shouldReturnUsersWithNameYogi() {

        // Arrange
        User user = new User();
        user.setUserName("Yogi");

        List<User> expectedUsers = List.of(user);

        when(mongoTemplate.find(
                any(Query.class),
                eq(User.class)
        )).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers =
                userRepository.getUserForSentimentAnalysis();

        // Assert
        assertEquals(expectedUsers, actualUsers);

        // Capture the Query passed to MongoTemplate
        ArgumentCaptor<Query> queryCaptor =
                ArgumentCaptor.forClass(Query.class);

        verify(mongoTemplate).find(
                queryCaptor.capture(),
                eq(User.class)
        );

        Query capturedQuery = queryCaptor.getValue();
        // Verify that the query searches for name = "Yogi"
        assertEquals("Yogi", capturedQuery.getQueryObject().getString("name"));
    }
}