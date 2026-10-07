package com.example.journalApp.controller;

import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "OK";
    }

    // Create a new PostMapping in UserController with "/register"
    /*@PostMapping("/create-user")
    public void createUser(@RequestBody UserEntity user) {
        // Password will save in encrypted format
        userService.saveNewUser(user);
    }*/

}
