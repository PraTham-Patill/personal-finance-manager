package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for login requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    // @NotBlank(message = "Email is required")
    // @Email(message = "Please provide a valid email address")
    private String email;

    // @NotBlank(message = "Password is required")
    private String password;
}
