package org.example.orderservices.controller;


import lombok.RequiredArgsConstructor;
import org.example.orderservices.dto.*;
import org.example.orderservices.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v5/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class OrderControllerV5 {

    private final OrderService orderService;

    /**
     * SCENARIO 1: Uspešno kreiranje (200 OK)
     * SCENARIO 2: Biznis greška (npr. 409 Conflict ako se cena promenila)
     * SCENARIO 3: Custom status (402 Payment Required ako je restaurantId = 99)
     */
    @PostMapping("/v5")
    public ResponseEntity<OrderResponseDTO> createOrderV5(@RequestBody OrderCreateDTO request) {
        OrderResponseDTO response = orderService.createOrderV5(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * SCENARIO 4: Testiranje Circuit Breaker-a i Fallback-a
     * Ako je Restaurant-service ugašen, poziv ove metode će aktivirati Fallback.
     * Zahvaljujući GlobalExceptionHandler-u, klijent dobija 503 JSON.
     */
    @GetMapping("/v5/test-circuit-breaker/{restaurantId}")
    public ResponseEntity<RestaurantDTO> testCB(@PathVariable Long restaurantId) {
        RestaurantDTO restaurant = orderService.getRestaurantSafe(restaurantId);
        return ResponseEntity.ok(restaurant);
    }
}