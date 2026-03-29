package org.example.orderservices.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new OrderProcessingException("Restoran servis kaže: Resurs nije nađen!", HttpStatus.NOT_FOUND);
            case 402 -> new OrderProcessingException("Restoran servis kaže: Nema sredstava!", HttpStatus.PAYMENT_REQUIRED);
            default -> new Exception("Generička Feign greška: " + response.status());
        };
    }
}