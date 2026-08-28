
package com.ompindia.controller;

import com.ompindia.entity.Booking;
import com.ompindia.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "${app.frontend.url}")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody Booking booking) {

        Booking savedBooking =
                bookingService.createBooking(booking);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.getBooking(id)
        );
    }

    @GetMapping("/pixel/{pixelNumber}")
    public ResponseEntity<Booking> getByPixel(
            @PathVariable Integer pixelNumber) {

        Booking booking =
                bookingService.getByPixel(pixelNumber);

        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(booking);
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<Booking>>
    getConfirmedBookings() {

        return ResponseEntity.ok(
                bookingService.getConfirmedBookings()
        );
    }
}
