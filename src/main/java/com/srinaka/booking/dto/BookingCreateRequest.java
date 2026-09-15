package com.srinaka.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record BookingCreateRequest(

        @NotNull
        Long serviceId,

        Long promotionId,

        @NotNull
        @Future
        Instant scheduledAt,

        @Size(max = 500)
        String notes
) {
}
