package com.skillswap.tutor.web;

import com.skillswap.tutor.dto.CreateTutorReviewRequest;
import com.skillswap.tutor.model.TutorReview;
import com.skillswap.tutor.service.TutorReviewService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
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
