package com.fintrack.personalfinancemanager.exception;

/**
 * Exception thrown when an operation cannot be performed due to business logic constraints.
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Constructs a new InvalidOperationException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOperationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an InvalidOperationException for a specific operation on a resource.
     *
     * @param operation the operation being attempted
     * @param resourceName the name of the resource
     * @param reason the reason why the operation is invalid
     * @return the exception
     */
    public static InvalidOperationException forOperation(String operation, String resourceName, String reason) {
        return new InvalidOperationException("Cannot perform " + operation + " on " + resourceName + ": " + reason);
    }

    /**
     * Creates an InvalidOperationException for a general operation.
     *
     * @param operation the operation being attempted
     * @param reason the reason why the operation is invalid
     * @return the exception
     */
    public static InvalidOperationException forOperation(String operation, String reason) {
        return new InvalidOperationException("Cannot perform " + operation + ": " + reason);
    }
}