package com.example.journalApp.controller;


import com.example.journalApp.cache.AppCache;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers()
    {
        List<UserEntity> users = userService.getAllEntries();

        if(users != null && !users.isEmpty()) {
            // Do something with the users
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/create-admin")
    //public ResponseEntity<?> createAdminUser(UserEntity user)
    public ResponseEntity<?> createAdminUser(@RequestBody UserEntity user)
    {
        // Password will save in encrypted format
        userService.saveNewAdminUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Autowired
    AppCache appCache;

    // This controller is being used because once your application is running and now db uri from
    // MongoDB Atlas is being changed, so we need to clear the cache and reinitialize the cache
    // So the updated value can read
    @GetMapping("/clear-api-cache")
    public void clearApiCache()
    {
        appCache.init();
    }
}
