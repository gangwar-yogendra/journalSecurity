package com.example.journalApp.service;

import com.example.journalApp.entity.User;
import com.example.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindByUserName() {
        User expectedUser = new User();
        expectedUser.setUserName("testUser");
        when(userRepository.findByUserName("testUser")).thenReturn(expectedUser);

        User actualUser = userService.findByUserName("testUser");

        assertEquals(expectedUser, actualUser);
        verify(userRepository).findByUserName("testUser");
    }
}
