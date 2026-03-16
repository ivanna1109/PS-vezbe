package org.example.orderservices.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long itemId;

    private Double priceAtBooking;

    @Min(value = 1, message = "Količina mora biti bar 1")
    private Integer quantity;
}
