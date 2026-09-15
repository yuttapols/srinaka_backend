package com.srinaka.catalog.dto;

public record ServiceCategoryResponse(
        Long id,
        String name,
        int sortOrder,
        boolean active
) {
}
