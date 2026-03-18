package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking){
        return bookingService.createBooking(booking);
    }

    @GetMapping("/student/{studentId}")
    public List<Booking> getStudentBookings(@PathVariable Long studentId){
        return bookingService.getStudentBookings(studentId);
    }

    @GetMapping("/tutor/{tutorId}")
    public List<Booking> getTutorBookings(@PathVariable Long tutorId){
        return bookingService.getTutorBookings(tutorId);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id){
        return bookingService.getBookingById(id);
    }

    @PutMapping("/{id}/approve")
    public Booking approveBooking(@PathVariable Long id){
        return bookingService.approveBooking(id);
    }

    @PutMapping("/{id}/reject")
    public Booking rejectBooking(@PathVariable Long id){
        return bookingService.rejectBooking(id);
    }
    
    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
            return bookingService.cancelBooking(id);
        
    }

    @PutMapping("/{id}/complete")
    public Booking completeBooking(@PathVariable Long id) {
        return bookingService.completeBooking(id);
    }

    @GetMapping("/tutor/{tutorId}/schedule")
    public List<Booking> getTutorSchedule(
        @PathVariable Long tutorId, 
        @RequestParam String sessionDate
    ) {
        return bookingService.getTutorSchedule(tutorId, sessionDate);
    }
    
}
