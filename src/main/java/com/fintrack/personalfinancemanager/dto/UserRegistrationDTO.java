package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration.
 * Includes password fields with validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    // Alias for email to support both 'email' and 'username' in requests
    public void setUsername(String username) {
        this.email = username;
    }

    public String getUsername() {
        return this.email;
    }

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;

    // Alias for phone to support both 'phone' and 'phoneNumber' in requests
    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phone;
    }
}