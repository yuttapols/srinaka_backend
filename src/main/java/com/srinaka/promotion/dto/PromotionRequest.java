package com.srinaka.promotion.dto;

import com.srinaka.promotion.domain.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @Size(max = 4000)
        String description,

        @NotNull
        DiscountType discountType,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal discountValue,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotNull
        Boolean active,

        @NotEmpty
        List<Long> serviceIds
) {
}
