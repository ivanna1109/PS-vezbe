package org.example.orderservices.exceptions;

import org.springframework.http.HttpStatus;

public class OrderProcessingException extends RuntimeException {
    private HttpStatus status;
    public OrderProcessingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() { return status; }
}