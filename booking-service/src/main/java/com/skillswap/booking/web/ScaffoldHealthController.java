package main.java.com.skillswap.booking.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScaffoldHealthController {

    @GetMapping("/api/bookings/health")
    public Map<String, Object> bookingsHealth() {
        return Map.of("service", "booking-service", "context", "bookings", "status", "SCAFFOLD");
    }

    @GetMapping("/api/student-profiles/health")
    public Map<String, Object> studentProfilesHealth() {
        return Map.of("service", "booking-service", "context", "student-profiles", "status", "SCAFFOLD");
    }
}