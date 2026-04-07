package com.example.demo.controller;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.TokenService;
import org.springframework.web.bind.annotation.*;

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
    public AuthResponseDTO register(@RequestBody RegisterRequestDTO request) {
        User user = registrationService.register(
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.getUserId(),
                request.getAdminCode()
        );

        String token = tokenService.generateToken(user);

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getUserId()
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
                user.getUserId()
        );
    }
}