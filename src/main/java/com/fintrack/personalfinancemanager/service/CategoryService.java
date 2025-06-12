package com.fintrack.personalfinancemanager.service;

import com.fintrack.personalfinancemanager.model.Category;

import java.util.List;

/**
 * Service interface for Category operations.
 */
public interface CategoryService {

    /**
     * Create a new category.
     *
     * @param category the category to create
     * @param userId the user ID
     * @return the created category
     * @throws IllegalArgumentException if a category with the same name already exists for the user
     */
    Category createCategory(Category category, Long userId);

    /**
     * Update an existing category.
     *
     * @param category the category with updated information
     * @param userId the user ID
     * @return the updated category
     * @throws IllegalArgumentException if the category does not exist or does not belong to the user
     */
    Category updateCategory(Category category, Long userId);

    /**
     * Delete a category by ID.
     *
     * @param categoryId the category ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the category does not exist, does not belong to the user, or cannot be deleted
     */
    void deleteCategory(Long categoryId, Long userId);

    /**
     * Find a category by ID.
     *
     * @param categoryId the category ID
     * @param userId the user ID
     * @return the category if found
     * @throws IllegalArgumentException if the category does not exist or does not belong to the user
     */
    Category findById(Long categoryId, Long userId);

    /**
     * Find all categories for a user.
     *
     * @param userId the user ID
     * @return list of categories
     */
    List<Category> findAllByUser(Long userId);

    /**
     * Find a category by name for a user.
     *
     * @param name the category name
     * @param userId the user ID
     * @return the category if found, null otherwise
     */
    Category findByNameAndUser(String name, Long userId);

    /**
     * Check if a category with the given name exists for a user.
     *
     * @param name the category name
     * @param userId the user ID
     * @return true if the category exists, false otherwise
     */
    boolean existsByNameAndUser(String name, Long userId);

    /**
     * Find all default categories.
     *
     * @return list of default categories
     */
    List<Category> findDefaultCategories();

    /**
     * Check if a category can be deleted.
     *
     * @param categoryId the category ID
     * @param userId the user ID
     * @return true if the category can be deleted, false otherwise
     * @throws IllegalArgumentException if the category does not exist or does not belong to the user
     */
    boolean canDeleteCategory(Long categoryId, Long userId);
}