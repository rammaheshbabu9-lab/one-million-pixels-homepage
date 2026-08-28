
package com.ompindia.service;

import com.ompindia.entity.Booking;
import com.ompindia.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private static final int TOTAL_ROWS = 100;
    private static final int BLOCKS_PER_ROW = 20;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking createBooking(Booking booking) {

        validatePixel(booking.getPixelNumber());

        if (bookingRepository.existsByPixelNumber(
                booking.getPixelNumber())) {

            throw new IllegalStateException(
                    "This pixel is already booked."
            );
        }

        String zone = calculateZone(
                booking.getPixelNumber()
        );

        int price = calculatePrice(zone);

        booking.setZone(zone);
        booking.setPrice(price);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found."
                        ));
    }

    public Booking getByPixel(Integer pixelNumber) {

        return bookingRepository
                .findByPixelNumber(pixelNumber)
                .orElse(null);
    }

    public List<Booking> getConfirmedBookings() {

        return bookingRepository.findByStatus(
                "CONFIRMED"
        );
    }

    private void validatePixel(Integer pixelNumber) {

        if (pixelNumber == null) {
            throw new IllegalArgumentException(
                    "Pixel number is required."
            );
        }

        int totalPixels =
                TOTAL_ROWS * BLOCKS_PER_ROW;

        if (pixelNumber < 1 ||
                pixelNumber > totalPixels) {

            throw new IllegalArgumentException(
                    "Invalid pixel number."
            );
        }
    }

    private String calculateZone(Integer pixelNumber) {

        int row =
                ((pixelNumber - 1) / BLOCKS_PER_ROW) + 1;

        int block =
                ((pixelNumber - 1) % BLOCKS_PER_ROW) + 1;

        boolean firstRow = row == 1;
        boolean lastRow = row == TOTAL_ROWS;

        boolean firstBlock = block == 1;
        boolean lastBlock = block == BLOCKS_PER_ROW;

        if ((firstRow || lastRow) &&
                (firstBlock || lastBlock)) {

            return "CORNER";
        }

        if (firstRow ||
                lastRow ||
                firstBlock ||
                lastBlock) {

            return "EDGE";
        }

        if (row >= 40 &&
                row <= 60 &&
                block >= 7 &&
                block <= 14) {

            return "PREMIUM";
        }

        return "CENTER";
    }

    private int calculatePrice(String zone) {

        return switch (zone) {

            case "CENTER" -> 99;

            case "PREMIUM" -> 199;

            case "EDGE" -> 299;

            case "CORNER" -> 499;

            default ->
                    throw new IllegalArgumentException(
                            "Invalid zone."
                    );
        };
    }
}
