package org.example.orderservices.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.OrderEventDTO;
import org.example.orderservices.repository.OrderRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderFeedbackListener {
    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = "order_feedback_queue")
    public void handleFeedback(Message message) {
        OrderEventDTO event = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Ručno čitamo bajtove i pretvaramo u DTO
            event = mapper.readValue(message.getBody(), OrderEventDTO.class);
            System.out.println("USPEŠNO PRIMLJEN EVENT ZA ORDER: " + event);
            System.out.println("Primljen feedback za porudžbinu..: "+event.getOrderId());
            OrderEventDTO finalEvent = event;
            orderRepository.findById(event.getOrderId()).ifPresent(order -> {
                order.setStatus(finalEvent.getStatus());
                orderRepository.save(order);
                System.out.println("Status porudžbine ažuriran na: " + order.getStatus());
            });
        } catch (Exception e){
            System.out.println("U order listeneru:"+e.getMessage());
        }

    }



}
