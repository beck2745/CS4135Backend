package com.skillswap.tutor.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScaffoldHealthController {

    @GetMapping("/api/tutors/health")
    public Map<String, Object> tutorsHealth() {
        return Map.of("service", "tutor-service", "context", "tutors", "status", "SCAFFOLD");
    }

    @GetMapping("/api/reviews/health")
    public Map<String, Object> reviewsHealth() {
        return Map.of("service", "tutor-service", "context", "reviews", "status", "SCAFFOLD");
    }
}
