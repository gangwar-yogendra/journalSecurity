package com.example.journalApp.service;

import com.example.journalApp.entity.User;
import com.example.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
/*@Component*/
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Attempting to load user by username: {}", username);

        User user = userRepository.findByUserName(username);
        if (user == null) {
            log.error("Authentication failed - username not found in database: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        List<String> roles = user.getRoles() == null ? List.of() : user.getRoles();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();

        log.info("User successfully loaded: {}", userDetails.getUsername());
        return userDetails;
    }
}
