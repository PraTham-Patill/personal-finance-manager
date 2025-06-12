package com.fintrack.personalfinancemanager.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new ResourceAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceAlreadyExistsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ResourceAlreadyExistsException for a resource with the given field value.
     *
     * @param resourceName the name of the resource
     * @param fieldName the name of the field
     * @param fieldValue the value of the field
     * @return the exception
     */
    public static ResourceAlreadyExistsException forResource(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceAlreadyExistsException(resourceName + " already exists with " + fieldName + " : " + fieldValue);
    }
}