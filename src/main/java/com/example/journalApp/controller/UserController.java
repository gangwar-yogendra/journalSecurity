package com.example.journalApp.controller;
import com.example.journalApp.api.response.WeatherResponse;
import com.example.journalApp.entity.User;
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

    // Update
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User userIndb = userService.findByUserName(userName);
        if(userIndb != null)
        {
            userIndb.setUserName(user.getUserName());
            userIndb.setPassword(user.getPassword());
            userService.updateEntry(userIndb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User userIndb = userService.findByUserName(userName);
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

        User userIndb = userService.findByUserName(userName);

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
