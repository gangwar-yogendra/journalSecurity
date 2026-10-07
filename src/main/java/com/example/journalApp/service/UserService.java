package com.example.journalApp.service;

import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.repository.UserRepository;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*@Component*/
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // This method saves a user to the database using the repository.
    // This function is used to save user with encrypted password
    public void saveNewUser(UserEntity user) {
        if (userRepository.findByUserName(user.getUserName()) != null) {
            throw new IllegalArgumentException(
                    "User already exists with username: " + user.getUserName()
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER")); // Set default role to USER
        userRepository.save(user);
    }

    // This business logic to add a new user in database as ADMIN
    public void saveNewAdminUser(UserEntity user) {

        if (userRepository.findByUserName(user.getUserName()) != null) {
            throw new IllegalArgumentException(
                    "User already exists with username: " + user.getUserName()
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER", "ADMIN")); // Set role to ADMIN
        userRepository.save(user);
    }

    // Save user details in db
    public void saveEntry(UserEntity user) {
        userRepository.save(user);
    }

    // Get all database entries
    public List<UserEntity> getAllEntries()
    {
        return userRepository.findAll();
    }

    // Get user details using db object id
    public Optional<UserEntity> getEntryById(ObjectId id) {
        return userRepository.findById(id);
    }

    // Delete user details using user object id
    public void deleteEntryById(ObjectId id) {
        userRepository.deleteById(id);
    }


    // Updated user in database
    public void updateEntry(UserEntity user) {
        /*user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER")); // Set default role to USER
        userRepository.save(user);*/

        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserEntity existingUser = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        existingUser.setEmail(user.getEmail());
        existingUser.setSentimentalAnalysisEnabled(
                user.isSentimentalAnalysisEnabled()
        );

        if (user.getPassword() != null &&
                !user.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        userRepository.save(existingUser);
    }

    // Find user details by username
    public UserEntity findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    // Delete user from db by username
    public void deleteByUserName(@NonNull String userName) {
        userRepository.deleteByUserName(userName);
    }
}
