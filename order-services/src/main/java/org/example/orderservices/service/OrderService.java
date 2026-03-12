package org.example.orderservices.service;

import lombok.RequiredArgsConstructor;
import org.example.orderservices.client.RestaurantClient;
import org.example.orderservices.dto.OrderCreateDTO;
import org.example.orderservices.dto.OrderResponseDTO;
import org.example.orderservices.dto.RestaurantDTO;
import org.example.orderservices.model.Order;
import org.example.orderservices.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order saveOrder(Order order) {
        // Ovde ćemo kasnije dodati logiku za proveru cena iz restaurant-service
        // Za sada samo računamo totalAmount prosto (ako ga dobijemo sa frontenda)
        if (order.getItems() != null) {
            double total = order.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            order.setTotalAmount(total);
        }
        return orderRepository.save(order);
    }

    //V2

    public List<OrderResponseDTO> getAllDTOs() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setRestaurantId(dto.getRestaurantId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Računanje ukupne cene (Simulacija dok nemamo komunikaciju)
        double calculatedTotal = dto.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * 500.0) // Fiktivna cena
                .sum();

        order.setTotalAmount(calculatedTotal);

        Order saved = orderRepository.save(order);
        return convertToResponseDTO(saved);
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO resp = new OrderResponseDTO();
        resp.setId(order.getId());
        resp.setTotalAmount(order.getTotalAmount());
        resp.setStatus(order.getStatus());
        resp.setCreatedAt(order.getCreatedAt());
        return resp;
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Porudžbina sa ID " + id + " ne postoji!");
        }
        orderRepository.deleteById(id);
    }

    //V3

    private final RestaurantClient restaurantClient;

    public OrderResponseDTO createOrderV3(OrderCreateDTO dto) {
        // Ako restoran ne postoji, ovde će pući FeignException (404)
        RestaurantDTO restaurant = restaurantClient.getRestaurantById(dto.getRestaurantId());

        //System.out.println("Kreiram porudžbinu za restoran: " + restaurant.getName());

        return createOrderNew(dto, restaurant);
    }

    public OrderResponseDTO createOrderNew(OrderCreateDTO dto, RestaurantDTO rdto) {
        Order order = new Order();
        order.setRestaurantId(rdto.getId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Računanje ukupne cene (Simulacija dok nemamo komunikaciju)
        double calculatedTotal = dto.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * 500.0) // Fiktivna cena
                .sum();

        order.setTotalAmount(calculatedTotal);

        Order saved = orderRepository.save(order);
        return convertToResponseDTO(saved);
    }
}
