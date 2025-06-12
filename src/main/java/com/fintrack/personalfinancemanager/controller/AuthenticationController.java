package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.config.JwtUtil;
import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.LoginRequest;
import com.fintrack.personalfinancemanager.dto.LoginResponse;
import com.fintrack.personalfinancemanager.dto.UserDTO;
import com.fintrack.personalfinancemanager.dto.UserRegistrationDTO;
import com.fintrack.personalfinancemanager.mapper.UserMapper;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param loginRequest the login credentials
     * @return the response entity containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        // Get the authenticated user
        User user = userService.findByEmail(loginRequest.getEmail());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId().toString());
        
        // Create response
        LoginResponse loginResponse = new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                token
        );
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
    }

    /**
     * Register a new user.
     *
     * @param registrationDTO the user registration data
     * @return the response entity
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            User user = userService.registerUser(registrationDTO);
            UserDTO userDTO = userMapper.toDTO(user);
            return new ResponseEntity<>(ApiResponse.success(userDTO, "User registered successfully"), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Registration failed: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}