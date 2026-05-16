package org.example.orderservices.service;

import org.example.orderservices.configs.RabbitMQConfig;
import org.example.orderservices.model.Order;
import org.example.dtos.OrderEventDTO;
import org.example.orderservices.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    OrderRepository orderRepository;

    public OrderEventDTO placeOrder(OrderEventDTO event) {
        Order newOrder = new Order();
        newOrder.setCustomerName(event.getCustomerName());
        newOrder.setStatus("PENDING");
        newOrder.setTotalAmount(event.getTotalPrice());
        newOrder = orderRepository.save(newOrder);
        event.setOrderId(newOrder.getId());
        System.out.println("Generisani ID ordera: "+event.getOrderId());
        System.out.println("Slanje porudžbine na restoran mikroservis...");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        return event;
    }
}