package com.srinaka.booking.dto;

import com.srinaka.booking.domain.BookingChannel;
import com.srinaka.booking.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(
        Long id,
        Long customerId,
        String customerName,
        String walkInCustomerName,
        Long serviceId,
        String serviceName,
        Long employeeId,
        String employeeName,
        Long promotionId,
        BookingChannel channel,
        Instant scheduledAt,
        BigDecimal finalPrice,
        BigDecimal discountAmount,
        BookingStatus status,
        String notes,
        Instant createdAt
) {
}
