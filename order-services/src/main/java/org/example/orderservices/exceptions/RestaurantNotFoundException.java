package org.example.orderservices.exceptions;

import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends RuntimeException {
    private HttpStatus status;
    public RestaurantNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() { return status; }
}
