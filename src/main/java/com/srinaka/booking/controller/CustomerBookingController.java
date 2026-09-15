package com.srinaka.booking.controller;

import com.srinaka.booking.dto.BookingCreateRequest;
import com.srinaka.booking.dto.BookingResponse;
import com.srinaka.booking.service.BookingService;
import com.srinaka.common.response.ApiResponse;
import com.srinaka.common.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(@Valid @RequestBody BookingCreateRequest request) {
        BookingResponse response = bookingService.create(SecurityUtils.currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Booking created successfully."));
    }

    @GetMapping("/my")
    public ApiResponse<List<BookingResponse>> myBookings() {
        return ApiResponse.success(bookingService.listForCustomer(SecurityUtils.currentUserId()));
    }
}
