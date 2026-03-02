package org.example.restaurantservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Naziv jela je obavezan")
    @Schema(example = "Pizza Capricciosa")
    private String name;

    @Positive(message = "Cena mora biti veća od 0")
    @Schema(example = "950.0")
    private Double price;

    @Schema(example = "Pelat, sir, šunka, šampinjoni")
    private String description;
}