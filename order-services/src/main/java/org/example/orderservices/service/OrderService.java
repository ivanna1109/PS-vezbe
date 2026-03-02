package org.example.orderservices.service;

import org.example.orderservices.model.Order;
import org.example.orderservices.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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
}
