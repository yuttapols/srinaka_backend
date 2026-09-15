package com.srinaka.catalog.dto;

import com.srinaka.catalog.domain.ServiceType;

import java.math.BigDecimal;

public record SpaServiceResponse(
        Long id,
        Long categoryId,
        String categoryName,
        ServiceType type,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        String imageUrl,
        boolean active
) {
}
