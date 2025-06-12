package com.fintrack.personalfinancemanager.service;

import com.fintrack.personalfinancemanager.dto.UserDTO;
import com.fintrack.personalfinancemanager.dto.UserRegistrationDTO;
import com.fintrack.personalfinancemanager.model.User;

/**
 * Service interface for User operations.
 */
public interface UserService {

    /**
     * Register a new user from a UserRegistrationDTO.
     *
     * @param registrationDTO the user registration data
     * @return the registered user
     * @throws IllegalArgumentException if the email is already in use or passwords don't match
     */
    User registerUser(UserRegistrationDTO registrationDTO);
    
    /**
     * Register a new user.
     *
     * @param user the user to register
     * @return the registered user
     * @throws IllegalArgumentException if the email is already in use
     */
    User registerUser(User user);

    /**
     * Find a user by email.
     *
     * @param email the email
     * @return the user if found, null otherwise
     */
    User findByEmail(String email);

    /**
     * Check if a user with the given email exists.
     *
     * @param email the email
     * @return true if the user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user with the given ID exists.
     *
     * @param userId the user ID
     * @return true if the user exists, false otherwise
     */
    boolean existsById(Long userId);

    /**
     * Update a user's information.
     *
     * @param user the user with updated information
     * @return the updated user
     * @throws IllegalArgumentException if the user does not exist
     */
    User updateUser(User user);
    
    /**
     * Update a user from a UserDTO.
     *
     * @param userId the user ID
     * @param userDTO the user data
     * @return the updated user
     * @throws IllegalArgumentException if the user is not found
     */
    User updateUser(Long userId, UserDTO userDTO);

    /**
     * Change a user's password.
     *
     * @param userId the user ID
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return the updated user
     * @throws IllegalArgumentException if the user does not exist or the old password is incorrect
     */
    User changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Delete a user by ID.
     *
     * @param userId the user ID
     * @throws IllegalArgumentException if the user does not exist
     */
    void deleteUser(Long userId);

    /**
     * Find a user by ID.
     *
     * @param userId the user ID
     * @return the user if found
     * @throws IllegalArgumentException if the user does not exist
     */
    User findById(Long userId);
    
    /**
     * Find a user by ID.
     *
     * @param userId the user ID
     * @return the user
     * @throws IllegalArgumentException if the user is not found
     */
    User findUserById(Long userId);

    /**
     * Authenticate a user with email and password.
     *
     * @param email the email
     * @param password the password
     * @return the authenticated user
     * @throws IllegalArgumentException if the credentials are invalid
     */
    User authenticateUser(String email, String password);
}