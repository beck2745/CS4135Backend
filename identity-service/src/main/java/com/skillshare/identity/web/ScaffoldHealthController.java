package com.skillswap.identity.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScaffoldHealthController {

    @GetMapping("/api/auth/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "identity-service",
                "status", "SCAFFOLD",
                "hint", "Copy auth code from demo/ into this service."
        );
    }
}