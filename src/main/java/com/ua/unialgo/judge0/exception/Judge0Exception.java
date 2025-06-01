package com.ua.unialgo.judge0.exception;

/**
 * Custom exception for Judge0 API related errors.
 * Provides specific error handling for Judge0 operations.
 */
public class Judge0Exception extends RuntimeException {

    private final String errorCode;
    private final Integer statusCode;

    public Judge0Exception(String message) {
        super(message);
        this.errorCode = null;
        this.statusCode = null;
    }

    public Judge0Exception(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.statusCode = null;
    }

    public Judge0Exception(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = null;
    }

    public Judge0Exception(String message, String errorCode, Integer statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public Judge0Exception(String message, String errorCode, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Judge0Exception: ");
        sb.append(getMessage());
        if (errorCode != null) {
            sb.append(" [Error Code: ").append(errorCode).append("]");
        }
        if (statusCode != null) {
            sb.append(" [Status Code: ").append(statusCode).append("]");
        }
        return sb.toString();
    }
}