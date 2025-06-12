package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.PasswordChangeDTO;
import com.fintrack.personalfinancemanager.dto.UserDTO;
import com.fintrack.personalfinancemanager.dto.UserRegistrationDTO;
import com.fintrack.personalfinancemanager.mapper.UserMapper;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user management operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Register a new user.
     *
     * @param registrationDTO the user registration data
     * @return the response entity
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        User user = userService.registerUser(registrationDTO);
        UserDTO userDTO = userMapper.toDTO(user);
        return new ResponseEntity<>(ApiResponse.success(userDTO, "User registered successfully"), HttpStatus.CREATED);
    }

    /**
     * Get the current user's profile.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@RequestParam Long userId) {
        // In a real application, this would be obtained from the security context
        User user = userService.findUserById(userId);
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }

    /**
     * Update the current user's profile.
     *
     * @param userId the ID of the authenticated user
     * @param userDTO the updated user data
     * @return the response entity
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @RequestParam Long userId,
            @Valid @RequestBody UserDTO userDTO) {
        // In a real application, this would be obtained from the security context
        User user = userService.updateUser(userId, userDTO);
        UserDTO updatedUserDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(ApiResponse.success(updatedUserDTO, "Profile updated successfully"));
    }

    /**
     * Change the current user's password.
     *
     * @param userId the ID of the authenticated user
     * @param passwordChangeDTO the password change data
     * @return the response entity
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        // In a real application, this would be obtained from the security context
        userService.changePassword(userId, passwordChangeDTO.getCurrentPassword(), 
                passwordChangeDTO.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /**
     * Delete the current user's account.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestParam Long userId) {
        // In a real application, this would be obtained from the security context
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully"));
    }
}