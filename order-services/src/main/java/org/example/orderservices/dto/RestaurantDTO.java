package org.example.orderservices.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private List<ItemDTO> menuItems;

    /*
    @NotBlank(message = "Naziv restorana je obavezan")
    @Schema(example = "Tramontana")
    private String name;

    @NotBlank(message = "Adresa je obavezna")
    @Schema(example = "Dunavska 16")
    private String address;

    //private List<ItemDTO> menuItems = new ArrayList<>();
    */

}
