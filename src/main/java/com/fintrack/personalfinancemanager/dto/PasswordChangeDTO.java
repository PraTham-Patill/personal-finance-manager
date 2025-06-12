package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Data Transfer Object for password change requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "New password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}