package com.hapex.inventory.utils;

import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Value
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public ErrorDetails(RuntimeException ex, WebRequest request) {
        this(new Date(), "Error: " + ex.getMessage(), request.getDescription(false));
    }

    public ResponseEntity<Object> generateResponse(HttpStatus status) {
        return new ResponseEntity<>(this, new HttpHeaders(), status);
    }
}
