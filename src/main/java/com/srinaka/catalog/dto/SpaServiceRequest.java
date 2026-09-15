package com.srinaka.catalog.dto;

import com.srinaka.catalog.domain.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SpaServiceRequest(

        Long categoryId,

        @NotNull
        ServiceType type,

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 4000)
        String description,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal price,

        @Min(1)
        Integer durationMinutes,

        @NotNull
        Boolean active
) {
}
