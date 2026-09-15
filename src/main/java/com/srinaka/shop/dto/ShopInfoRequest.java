package com.srinaka.shop.dto;

import jakarta.validation.constraints.Size;

public record ShopInfoRequest(

        @Size(max = 2000)
        String address,

        @Size(max = 20)
        String phone,

        @Size(max = 150)
        String email,

        @Size(max = 500)
        String mapUrl,

        @Size(max = 2000)
        String businessHours,

        @Size(max = 100)
        String lineId,

        @Size(max = 500)
        String facebookUrl,

        @Size(max = 500)
        String instagramUrl
) {
}
