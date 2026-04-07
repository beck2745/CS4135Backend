package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.AccountSuspendedException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.BlockedContentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.valueobject.AccountState;
import com.example.demo.valueobject.ContentType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlockedContentRepository blockedContentRepository;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 BlockedContentRepository blockedContentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blockedContentRepository = blockedContentRepository;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (user.getStatus() == AccountState.SUSPENDED) {
            throw new AccountSuspendedException("Suspended users cannot log in");
        }

        if (blockedContentRepository.existsByContentTypeAndContentId(ContentType.USER, user.getUserId())) {
            throw new AccountSuspendedException("This account has been blocked");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return user;
    }
}