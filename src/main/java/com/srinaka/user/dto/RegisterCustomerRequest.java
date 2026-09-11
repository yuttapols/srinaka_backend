package com.srinaka.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotBlank
        @Size(max = 150)
        String fullName,

        @NotBlank
        @Pattern(regexp = "^0[0-9]{8,9}$", message = "phone must be a valid Thai phone number")
        String phone
) {
}
