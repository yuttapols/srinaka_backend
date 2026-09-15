package com.srinaka.booking.controller;

import com.srinaka.booking.dto.BookingResponse;
import com.srinaka.booking.service.BookingService;
import com.srinaka.common.response.ApiResponse;
import com.srinaka.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee/bookings")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<List<BookingResponse>> myBookings() {
        return ApiResponse.success(bookingService.listForEmployee(SecurityUtils.currentUserId()));
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<BookingResponse> complete(@PathVariable Long id) {
        return ApiResponse.success(bookingService.markCompleted(id, SecurityUtils.currentUserId()), "Booking marked as completed.");
    }
}
