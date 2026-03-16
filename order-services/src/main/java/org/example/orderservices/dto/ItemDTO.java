package org.example.orderservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Double price;
}
