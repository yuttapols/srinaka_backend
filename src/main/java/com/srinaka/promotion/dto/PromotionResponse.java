package com.srinaka.promotion.dto;

import com.srinaka.promotion.domain.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionResponse(
        Long id,
        String title,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        List<Long> serviceIds
) {
}
