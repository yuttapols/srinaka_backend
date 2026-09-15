package com.srinaka.booking.dto;

import jakarta.validation.constraints.NotNull;

public record BookingAssignRequest(

        @NotNull
        Long employeeId
) {
}
