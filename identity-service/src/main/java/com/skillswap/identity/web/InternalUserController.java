package com.skillswap.identity.web;

import com.skillswap.identity.dto.UserAdminDTO;
import com.skillswap.identity.dto.UserPublicDTO;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.exception.ResourceNotFoundException;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.identity.valueobject.AccountState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Cross-context ACL: other services validate user ids without coupling to this schema.
 */
@RestController
@RequestMapping("/api/internal/users")
public class InternalUserController {

    private final UserRepository userRepository;

    public InternalUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/exists")
    public Map<String, Boolean> exists(@PathVariable Long userId) {
        return Map.of("exists", userRepository.existsByUserId(userId));
    }

    @GetMapping("/{userId}")
    public UserAdminDTO getById(@PathVariable Long userId) {
        User u = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserAdminDTO(u.getUserId(), u.getEmail(), u.getRole().name(), u.getStatus().name());
    }

    @PostMapping("/{userId}/suspend")
    public void suspend(@PathVariable Long userId) {
        User u = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setStatus(AccountState.SUSPENDED);
        userRepository.save(u);
    }

    @PostMapping("/{userId}/activate")
    public void activate(@PathVariable Long userId) {
        User u = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setStatus(AccountState.ACTIVE);
        userRepository.save(u);
    }

    @PostMapping("/resolve")
    public List<UserPublicDTO> resolve(@RequestBody List<Long> userIds) {
        return userIds.stream()
                .distinct()
                .map(userRepository::findByUserId)
                .flatMap(java.util.Optional::stream)
                .map(u -> new UserPublicDTO(u.getUserId(), u.getName(), u.getEmail()))
                .toList();
    }
}