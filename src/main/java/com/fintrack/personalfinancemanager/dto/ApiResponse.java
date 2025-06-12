package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for standardized response format.
 *
 * @param <T> the type of data contained in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object errors;

    /**
     * Create a success response with data.
     *
     * @param data the response data
     * @param message the success message
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a success response with data and default message.
     *
     * @param data the response data
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    /**
     * Create a success response with message only.
     *
     * @param message the success message
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * Create an error response.
     *
     * @param message the error message
     * @param errors the detailed errors
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Create an error response with message only.
     *
     * @param message the error message
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
}