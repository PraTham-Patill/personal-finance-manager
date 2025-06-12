package com.fintrack.personalfinancemanager.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ResourceNotFoundException for a resource with the given ID.
     *
     * @param resourceName the name of the resource
     * @param fieldName the name of the field
     * @param fieldValue the value of the field
     * @return the exception
     */
    public static ResourceNotFoundException forResource(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(resourceName + " not found with " + fieldName + " : " + fieldValue);
    }
}