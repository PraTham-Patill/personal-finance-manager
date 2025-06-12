package com.fintrack.personalfinancemanager.repository;

import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Category entity operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories for a specific user.
     *
     * @param user the user
     * @return list of categories
     */
    List<Category> findByUser(User user);

    /**
     * Find all default categories.
     *
     * @return list of default categories
     */
    List<Category> findByIsDefaultTrue();

    /**
     * Find a category by name and user.
     *
     * @param name the category name
     * @param user the user
     * @return the category if found
     */
    Category findByNameAndUser(String name, User user);

    /**
     * Check if a category with the given name exists for a user.
     *
     * @param name the category name
     * @param user the user
     * @return true if the category exists, false otherwise
     */
    boolean existsByNameAndUser(String name, User user);

    /**
     * Find a default category by name.
     *
     * @param name the category name
     * @return the default category if found
     */
    Category findByNameAndIsDefaultTrue(String name);
}