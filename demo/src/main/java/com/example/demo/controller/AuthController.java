package com.example.demo.controller;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.UpdateProfileRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.service.StudentProfileService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    public AuthController(RegistrationService registrationService,
                          AuthenticationService authenticationService,
                          TokenService tokenService) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        User user = registrationService.register(
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.getUserId(),
                request.getAdminCode(),
                request.getName()
        );

        String token = tokenService.generateToken(user);

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getUserId(),
                user.getName()
        );
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        User user = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        String token = tokenService.generateToken(user);

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getUserId(),
                user.getName()
        );
    }

    @PutMapping("/profile/{userId}")
public AuthResponseDTO updateProfile(
        @PathVariable Long userId,
        @RequestBody UpdateProfileRequest request
) {
    User user = authenticationService.updateProfile(userId, request.getName());

    String token = tokenService.generateToken(user);

    return new AuthResponseDTO(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getStatus().name(),
            user.getUserId(),
            user.getName()
    );
}

@GetMapping("/profile/{userId}")
public AuthResponseDTO getProfile(@PathVariable Long userId) {
    User user = authenticationService.getUserById(userId);
    String token = tokenService.generateToken(user);
    return new AuthResponseDTO(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getStatus().name(),
            user.getUserId(),
            user.getName()
    );
}

}