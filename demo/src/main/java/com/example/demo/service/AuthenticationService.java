package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.AccountSuspendedException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.valueobject.AccountState;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (user.getStatus() == AccountState.SUSPENDED) {
            throw new AccountSuspendedException("Suspended users cannot log in");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return user;
    }
     public User updateProfile(Long userId, String name) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}