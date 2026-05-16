package org.example.orderservices.controller;
import lombok.RequiredArgsConstructor;
import org.example.orderservices.configs.RabbitMQConfig;
import org.example.orderservices.dto.*;
import org.example.dtos.OrderEventDTO;
import org.example.orderservices.service.OrderProducer;
import org.example.orderservices.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v5/orders")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:8080")
@RefreshScope
public class OrderControllerV5 {

    //v6
    @Value("${custom.welcome-message}")
    private String message;

    @GetMapping("/api/v5/orders/welcome")
    public String getMessage() { return message; }

    //*****************************************************************************

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

    //vezbe 9
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/test-rabbit")
    public String send() {
        String poruka = "Pozdrav sa RabbitMQ-a!";
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, poruka);
        return "Poruka poslata!";
    }


    //vezbe 10

    private final OrderProducer orderProducer;
    @PostMapping("/rabbitMQ/createOrder")
    public ResponseEntity<OrderEventDTO> createOrderV5(@RequestBody OrderEventDTO request) {
        request = orderProducer.placeOrder(request);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @GetMapping("/rabbitMQ/getOrder/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(id));
    }
}