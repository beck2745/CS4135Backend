package com.skillswap.identity.service;

import com.skillswap.identity.entity.User;
import com.skillswap.identity.exception.EmailAlreadyExistsException;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.identity.valueobject.AccountState;
import com.skillswap.identity.valueobject.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private static final String ADMIN_SECRET = "admin123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password, UserRole role, Long userId, String adminCode, String name) {
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