package com.srinaka.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LineLoginRequest(

        @NotBlank
        String code,

        @NotBlank
        String redirectUri
) {
}
