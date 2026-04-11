package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.valueobject.AccountState;
import com.example.demo.valueobject.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_savesUser_whenEmailIsUnique() {
        when(userRepository.existsByEmail("test@ul.ie")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = registrationService.register(
                "test@ul.ie", "password123", UserRole.STUDENT, 1L, null, "Alice"
        );

        assertThat(result.getEmail()).isEqualTo("test@ul.ie");
        assertThat(result.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(result.getStatus()).isEqualTo(AccountState.ACTIVE);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailIsDuplicate() {
        when(userRepository.existsByEmail("existing@ul.ie")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(
                "existing@ul.ie", "password", UserRole.STUDENT, 2L, null, "Bob"
        )).isInstanceOf(EmailAlreadyExistsException.class)
          .hasMessageContaining("Email already exists");
    }

    @Test
    void register_throwsIllegalArgument_whenAdminCodeIsWrong() {
        when(userRepository.existsByEmail("admin@ul.ie")).thenReturn(false);

        assertThatThrownBy(() -> registrationService.register(
                "admin@ul.ie", "password", UserRole.ADMIN, 3L, "wrongcode", "Charlie"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid admin code");
    }

    @Test
    void register_succeeds_whenCorrectAdminCodeProvided() {
        when(userRepository.existsByEmail("admin@ul.ie")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = registrationService.register(
                "admin@ul.ie", "password", UserRole.ADMIN, 4L, "admin123", "Dave"
        );

        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
    }
}
