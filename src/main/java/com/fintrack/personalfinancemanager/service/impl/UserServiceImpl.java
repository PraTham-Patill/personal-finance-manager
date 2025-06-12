package com.fintrack.personalfinancemanager.service.impl;

import com.fintrack.personalfinancemanager.dto.UserDTO;
import com.fintrack.personalfinancemanager.dto.UserRegistrationDTO;
import com.fintrack.personalfinancemanager.mapper.UserMapper;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.repository.CategoryRepository;
import com.fintrack.personalfinancemanager.repository.UserRepository;
import com.fintrack.personalfinancemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the UserService interface.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Default categories to be created for new users.
     */
    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "Housing", "Transportation", "Food", "Utilities", "Insurance",
            "Medical & Healthcare", "Saving, Investing, & Debt Payments", "Personal Spending",
            "Recreation & Entertainment", "Miscellaneous", "Salary", "Bonus", "Investment", "Other Income"
    );

    /**
     * Register a new user from a UserRegistrationDTO.
     *
     * @param registrationDTO the user registration data
     * @return the registered user
     * @throws IllegalArgumentException if the email is already in use or passwords don't match
     */
    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        // Validate that passwords match
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Convert DTO to entity
        User user = userMapper.toEntity(registrationDTO);
        
        // Register the user
        return registerUser(user);
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        User savedUser = userRepository.save(user);

        // Create default categories for the user
        createDefaultCategories(savedUser);

        return savedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update fields
        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());

        // Don't update email if it's already in use by another user
        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        existingUser.setEmail(user.getEmail());

        // Don't update password here, use changePassword method instead

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Update a user from a UserDTO.
     *
     * @param userId the user ID
     * @param userDTO the user data
     * @return the updated user
     * @throws IllegalArgumentException if the user is not found
     */
    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) {
        User existingUser = findById(userId);
        User updatedUser = userMapper.updateEntityFromDTO(existingUser, userDTO);
        return updateUser(updatedUser);
    }

    /**
     * Find a user by ID.
     *
     * @param userId the user ID
     * @return the user
     * @throws IllegalArgumentException if the user is not found
     */
    public User findUserById(Long userId) {
        return findById(userId);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    /**
     * Create default categories for a new user.
     *
     * @param user the user
     */
    private void createDefaultCategories(User user) {
        for (String categoryName : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setName(categoryName);
            category.setUser(user);
            category.setDefault(false);
            categoryRepository.save(category);
        }
    }
}