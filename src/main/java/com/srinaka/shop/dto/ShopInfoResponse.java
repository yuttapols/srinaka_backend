package com.srinaka.shop.dto;

public record ShopInfoResponse(
        String address,
        String phone,
        String email,
        String mapUrl,
        String businessHours,
        String lineId,
        String facebookUrl,
        String instagramUrl
) {
}
