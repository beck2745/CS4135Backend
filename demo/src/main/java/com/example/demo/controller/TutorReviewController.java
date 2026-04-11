package com.example.demo.controller;

import com.example.demo.dto.CreateTutorReviewRequest;
import com.example.demo.model.TutorReview;
import com.example.demo.service.TutorReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class TutorReviewController {

    private final TutorReviewService tutorReviewService;

    public TutorReviewController(TutorReviewService tutorReviewService) {
        this.tutorReviewService = tutorReviewService;
    }

    @PostMapping
    public TutorReview createReview(@RequestBody CreateTutorReviewRequest request) {
        return tutorReviewService.createReview(request);
    }

    @GetMapping("/tutor/{tutorId}")
    public List<TutorReview> getTutorReviews(@PathVariable Long tutorId) {
        return tutorReviewService.getTutorReviews(tutorId);
    }

    @GetMapping("/booking/{bookingId}")
    public TutorReview getReviewByBooking(@PathVariable Long bookingId) {
        return tutorReviewService.getReviewByBookingId(bookingId);
    }

    @GetMapping("/tutor/{tutorId}/average")
    public Map<String, Double> getTutorAverage(@PathVariable Long tutorId) {
        return Map.of("averageRating", tutorReviewService.getAverageRating(tutorId));
    }
}