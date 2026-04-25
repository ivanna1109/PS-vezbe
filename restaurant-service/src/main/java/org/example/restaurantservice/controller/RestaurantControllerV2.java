package org.example.restaurantservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantservice.dto.RestaurantDTO;
import org.example.restaurantservice.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/restaurants")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:8080")
public class RestaurantControllerV2 {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAll() {
        return ResponseEntity.ok(restaurantService.getAll());
    }

    @PostMapping
    @Operation(summary = "Dodaj novi restoran")
    @ApiResponse(responseCode = "201", description = "Restoran je uspešno kreiran")
    public ResponseEntity<RestaurantDTO> create(@Valid @RequestBody RestaurantDTO dto) {
        // @Valid pokreće Bean Validation
        return ResponseEntity.status(201).body(restaurantService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Izmeni podatke o restoranu")
    public ResponseEntity<RestaurantDTO> update(@PathVariable Long id, @Valid @RequestBody RestaurantDTO dto) {
        return ResponseEntity.ok(restaurantService.update(id, dto));
    }

    // V3

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getById(@PathVariable Long id) {
        // Ova metoda mora postojati u RestaurantService-u
        return ResponseEntity.ok(restaurantService.getById(id));
    }
}
