package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.valueobject.AccountState;
import com.example.demo.valueobject.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(email, hashedPassword, role, AccountState.ACTIVE);
        return userRepository.save(user);
    }
}