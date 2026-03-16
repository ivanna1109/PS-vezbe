package org.example.restaurantservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Naziv restorana je obavezan")
    @Schema(example = "Tramontana")
    private String name;

    @NotBlank(message = "Adresa je obavezna")
    @Schema(example = "Dunavska 16")
    private String address;

    //v4 - potrebno dodati!
    private List<ItemDTO> menuItems = new ArrayList<>();
}
