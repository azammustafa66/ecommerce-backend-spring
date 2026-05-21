package com.spring.ecommerce.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError<T> {
    private final int statusCode;
    private final T errors;
    private final String message;
    private String stackTrace;

    public ApiError(int statusCode, T errors, String message, String stackTrace) {
        this.statusCode = statusCode;
        this.errors = errors;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
