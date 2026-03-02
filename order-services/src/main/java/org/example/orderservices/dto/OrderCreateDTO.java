package org.example.orderservices.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDTO {
    @NotNull(message = "Morate odabrati restoran")
    private Long restaurantId;

    @NotEmpty(message = "Porudžbina ne može biti prazna")
    private List<OrderItemRequestDTO> items;
}
