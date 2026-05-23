package com.spring.ecommerce.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIError<T> {

    private final int statusCode;
    private final String message;
    private final boolean success;
    private final T errors;
    private final String stack;

    public APIError(int statusCode, String message) {
        this(statusCode, message, null, null);
    }

    public APIError(int statusCode, String message, T errors) {
        this(statusCode, message, errors, null);
    }

    public APIError(int statusCode, String message, T errors, String stack) {
        this.statusCode = statusCode;
        this.message = message;
        this.success = false;
        this.errors = errors;
        this.stack = stack;
    }
}
