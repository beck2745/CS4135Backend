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

    private static final String ADMIN_SECRET = "admin123";

<<<<<<< HEAD
    public User register(String email, String password, UserRole role, Long userId, String adminCode, String name) {
=======
    public User register(String email, String password, UserRole role, Long userId, String adminCode) {
>>>>>>> origin/main
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (role == UserRole.ADMIN && !ADMIN_SECRET.equals(adminCode)) {
            throw new IllegalArgumentException("Invalid admin code");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(email, hashedPassword, role, AccountState.ACTIVE, userId, name);
        return userRepository.save(user);
    }
}