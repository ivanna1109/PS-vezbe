package org.example.orderservices.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new OrderProcessingException("Restoran servis kaže: Resurs nije nađen!", HttpStatus.NOT_FOUND);
            case 401 -> new OrderProcessingException("Greška u autentifikaciji između servisa!", HttpStatus.UNAUTHORIZED);
            case 403 -> new OrderProcessingException("Zabranjen pristup ovom resursu!", HttpStatus.FORBIDDEN);
            case 429 -> new OrderProcessingException("Sistem je preopterećen, usporite sa zahtevima!", HttpStatus.TOO_MANY_REQUESTS);
            case 402 -> new OrderProcessingException("Restoran servis kaže: Nema sredstava!", HttpStatus.PAYMENT_REQUIRED);
            default -> new OrderProcessingException("Nepoznata greška pri komunikaciji: " + response.status(), HttpStatus.BAD_GATEWAY);
        };
    }
}