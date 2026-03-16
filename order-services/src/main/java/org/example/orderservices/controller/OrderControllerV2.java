package org.example.orderservices.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.orderservices.dto.OrderCreateDTO;
import org.example.orderservices.dto.OrderResponseDTO;
import org.example.orderservices.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/orders") // Verzija 2
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAllDTOs());
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderCreateDTO dto) {
        //return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrderV3(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši porudžbinu iz sistema")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build(); // Vraća status 204 - adekvatno za brisanje bez propratne poruke
    }

    //v4
    @PostMapping("/newOrderV4")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrderV4(dto));
    }
}
