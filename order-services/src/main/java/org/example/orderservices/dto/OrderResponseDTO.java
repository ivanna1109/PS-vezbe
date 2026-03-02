package org.example.orderservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private Long id;
    private Double totalAmount;
    private String status;
    @Schema(example = "2024-05-20T10:15:30")
    private LocalDateTime createdAt;
}
