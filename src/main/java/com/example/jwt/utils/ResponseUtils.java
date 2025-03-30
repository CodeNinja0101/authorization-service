package com.example.jwt.utils;

import com.example.jwt.dto.ApiResponse;

public class ResponseUtils {

    public static <T> ApiResponse<T> successfulRequestResponse(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static ApiResponse<Void> conflictResponse(String message) {
        return new ApiResponse<>(409, message, null);
    }

    public static ApiResponse<Void> notFoundResponse(String message) {
        return new ApiResponse<>(404, message, null);
    }

    public static ApiResponse<Void> unauthorizedResponse(String message) {
        return new ApiResponse<>(401, message, null);
    }

    public static ApiResponse<Void> internalServerErrorResponse(String message) {
        return new ApiResponse<>(500, message, null);
    }
}