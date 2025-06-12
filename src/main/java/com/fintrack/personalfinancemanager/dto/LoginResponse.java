package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for login responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String name; // Maps to User.fullName
    private String email;
    private String token;
}