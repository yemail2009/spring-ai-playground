package com.ai.dataCleansing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                "DATA_CLEANING_ERROR",
                e.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException e) {
        ErrorResponse error = new ErrorResponse(
                "JSON_PARSING_ERROR",
                "JSON解析失败: " + e.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private long timestamp;

        public ErrorResponse(String errorCode, String message, long timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getter和Setter
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
