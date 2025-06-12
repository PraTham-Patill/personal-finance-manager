package com.fintrack.personalfinancemanager.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedAccessException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthorizedAccessException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an UnauthorizedAccessException for a resource with the given ID.
     *
     * @param resourceName the name of the resource
     * @param resourceId the ID of the resource
     * @return the exception
     */
    public static UnauthorizedAccessException forResource(String resourceName, Long resourceId) {
        return new UnauthorizedAccessException("You do not have permission to access " + resourceName + " with ID: " + resourceId);
    }

    /**
     * Creates an UnauthorizedAccessException for a general operation.
     *
     * @param operation the operation being attempted
     * @return the exception
     */
    public static UnauthorizedAccessException forOperation(String operation) {
        return new UnauthorizedAccessException("You do not have permission to perform this operation: " + operation);
    }
}