package com.example.journalApp.controller;
import com.example.journalApp.api.response.WeatherResponse;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.service.UserService;
import com.example.journalApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        log.info("Register request received for username: {}", user.getUserName());

        try {
            userService.saveNewUser(user);

            log.info("User registered successfully: {}", user.getUserName());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully");

        } catch (IllegalArgumentException e) {

            log.warn("Registration failed: {}", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // Update
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserEntity user) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserName = authentication.getName();

        try {
            UserEntity existingUser =
                    userService.findByUserName(currentUserName);

            if (existingUser == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

            existingUser.setEmail(user.getEmail());
            existingUser.setSentimentalAnalysisEnabled(
                    user.isSentimentalAnalysisEnabled()
            );

            if (user.getPassword() != null &&
                    !user.getPassword().isBlank()) {

                existingUser.setPassword(user.getPassword());
            }

            userService.updateEntry(existingUser);

            log.info("User updated successfully: {}", currentUserName);

            return ResponseEntity.ok("User updated successfully");

        } catch (IllegalArgumentException e) {

            log.warn("User update failed: {}", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        UserEntity userIndb = userService.findByUserName(userName);
        if(userIndb != null)
        {
            userService.deleteByUserName(userIndb.getUserName());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // this code is being added because when we Integrate the External API
    @GetMapping
    public ResponseEntity<?> greeting() {
        log.info("Inside greeting method of UserController");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        log.info("Authenticated user: {}", userName);

        UserEntity userIndb = userService.findByUserName(userName);

        log.info("User found in database: {}", userIndb != null ? userIndb.getUserName() : "null");

        if(userIndb != null)
        {
            // Replace "City" with the actual city you want to get the weather for. For example, "Bareilly".
            WeatherResponse weatherResponse = weatherService.getWeather("Bareilly");
            if(weatherResponse != null) {
                return new ResponseEntity<>("Hi " + userIndb.getUserName() + ". The weather is " + weatherResponse.getWeatherDescription(), HttpStatus.OK);
            }

            log.warn("Weather response is null for user: {}", userName);
            return new ResponseEntity<>("Hi " + userIndb.getUserName() + ". Weather information is currently unavailable.", HttpStatus.OK);
        }

        log.error("User not found in database for username: {}", userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
