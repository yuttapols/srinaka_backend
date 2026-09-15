package com.srinaka.booking.controller;

import com.srinaka.booking.dto.BookingAssignRequest;
import com.srinaka.booking.dto.BookingResponse;
import com.srinaka.booking.service.BookingService;
import com.srinaka.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/bookings")
@RequiredArgsConstructor
public class SupervisorBookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR','EMPLOYEE')")
    public ApiResponse<List<BookingResponse>> list() {
        return ApiResponse.success(bookingService.listAll());
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ApiResponse<BookingResponse> assign(@PathVariable Long id, @Valid @RequestBody BookingAssignRequest request) {
        return ApiResponse.success(bookingService.assignEmployee(id, request.employeeId()), "Employee assigned successfully.");
    }
}
