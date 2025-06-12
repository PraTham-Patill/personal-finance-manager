package com.fintrack.personalfinancemanager.mapper;

import com.fintrack.personalfinancemanager.dto.UserDTO;
import com.fintrack.personalfinancemanager.dto.UserRegistrationDTO;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between User entity and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDTO.
     *
     * @param user the user entity
     * @return the user DTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    /**
     * Convert UserDTO to User entity.
     *
     * @param userDTO the user DTO
     * @return the user entity
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDTO.getId());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        return user;
    }

    /**
     * Convert UserRegistrationDTO to User entity.
     *
     * @param registrationDTO the registration DTO
     * @return the user entity
     */
    public User toEntity(UserRegistrationDTO registrationDTO) {
        if (registrationDTO == null) {
            return null;
        }
        
        User user = new User();
        user.setFullName(registrationDTO.getFullName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword()); // Note: This will be encoded by the service
        user.setPhone(registrationDTO.getPhone());
        
        return user;
    }

    /**
     * Update User entity from UserDTO.
     *
     * @param user the user entity to update
     * @param userDTO the user DTO with updated values
     * @return the updated user entity
     */
    public User updateEntityFromDTO(User user, UserDTO userDTO) {
        if (user == null || userDTO == null) {
            return user;
        }
        
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        return user;
    }
}